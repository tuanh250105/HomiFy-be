# train.py
import os
import json
import argparse
from datetime import datetime

import numpy as np
import pandas as pd
import joblib

from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score

from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestRegressor, HistGradientBoostingRegressor
from sklearn.linear_model import Ridge


# =========================
# 47 FEATURES ORDER (fixed)
# =========================
FEATURES_ORDER = [
    "usableArea",
    "totalArea",
    "bedrooms",
    "bathrooms",
    "floors",
    "yearBuilt",

    "propertyType_SINGLE_HOUSE",
    "propertyType_TOWN_HOUSE",
    "propertyType_APARTMENT",
    "propertyType_VILLA",

    "direction_EAST",
    "direction_WEST",
    "direction_SOUTH",
    "direction_NORTH",
    "direction_NORTHEAST",
    "direction_NORTHWEST",
    "direction_SOUTHEAST",
    "direction_SOUTHWEST",

    "landArea",
    "frontWidth",
    "roadWidth",
    "depth",
    "frontyardArea",
    "backyardArea",
    "lotArea",
    "gardenArea",
    "parkingSpaces",
    "floor",

    "hasPool",
    "hasGym",
    "hasHomeTheater",
    "hasGameRoom",

    "hasSecurityCamera",
    "has24hSecurity",
    "hasSmartLock",
    "hasSecurityDoor",

    "hasGarden",
    "hasPlayground",
    "hasRooftop",

    "hasDishwasher",
    "hasDryer",
    "hasMicrowave",
    "hasOven",
    "hasRefrigerator",
    "hasWasher",

    "walkScore",
    "bikeScore",
    "transitScore",
    "quietness",
    "securityLevel",

    "totalRooms",
    "propertyAge",
]

TARGET_COL = "price"


def ensure_dir(path: str):
    os.makedirs(path, exist_ok=True)


def compute_metrics(y_true, y_pred):
    mae = mean_absolute_error(y_true, y_pred)
    mse = mean_squared_error(y_true, y_pred)
    rmse = np.sqrt(mse)
    r2 = r2_score(y_true, y_pred)
    return {
        "MAE": float(mae),
        "RMSE": float(rmse),
        "R2": float(r2),
    }




def build_model(model_name: str, use_scaler: bool, seed: int):
    """
    Returns (pipeline, needs_scaler_file)
    - For tree models, scaler is optional (usually not needed).
    - For linear models, scaler is recommended.
    """
    model_name = model_name.lower().strip()

    if model_name == "ridge":
        reg = Ridge(alpha=1.0, random_state=seed)
        if use_scaler:
            pipe = Pipeline([
                ("scaler", StandardScaler()),
                ("model", reg),
            ])
        else:
            pipe = Pipeline([
                ("model", reg),
            ])
        return pipe

    if model_name == "rf":
        reg = RandomForestRegressor(
            n_estimators=400,
            random_state=seed,
            n_jobs=-1,
            max_depth=None,
            min_samples_split=2,
            min_samples_leaf=1,
        )
        # RF không cần scaler, nhưng nếu bạn bật use_scaler vẫn OK
        if use_scaler:
            pipe = Pipeline([
                ("scaler", StandardScaler()),
                ("model", reg),
            ])
        else:
            pipe = Pipeline([
                ("model", reg),
            ])
        return pipe

    if model_name == "hgb":
        reg = HistGradientBoostingRegressor(
            random_state=seed,
            max_depth=10,
            learning_rate=0.08,
            max_iter=600,
        )
        # HGB cũng không bắt buộc scaler
        if use_scaler:
            pipe = Pipeline([
                ("scaler", StandardScaler()),
                ("model", reg),
            ])
        else:
            pipe = Pipeline([
                ("model", reg),
            ])
        return pipe

    raise ValueError("model_name must be one of: ridge, rf, hgb")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--data", type=str, default="mock_property_data.csv", help="Path to CSV dataset")
    parser.add_argument("--artifacts", type=str, default="ml", help="Output folder for saved model/scaler/features")
    parser.add_argument("--model", type=str, default="hgb", choices=["ridge", "rf", "hgb"], help="Model type")
    parser.add_argument("--test_size", type=float, default=0.2, help="Test ratio")
    parser.add_argument("--seed", type=int, default=2025, help="Random seed")
    parser.add_argument("--use_scaler", action="store_true", help="Add StandardScaler to pipeline")
    args = parser.parse_args()

    ensure_dir(args.artifacts)

    # ======================
    # 1) Load data
    # ======================
    if not os.path.exists(args.data):
        raise FileNotFoundError(f"Dataset not found: {args.data}")

    df = pd.read_csv(args.data)

    # ======================
    # 2) Validate columns
    # ======================
    missing_features = [c for c in FEATURES_ORDER if c not in df.columns]
    if missing_features:
        raise ValueError(
            "Missing required feature columns in CSV:\n"
            + "\n".join(missing_features)
        )

    if TARGET_COL not in df.columns:
        raise ValueError(f"Missing target column '{TARGET_COL}' in CSV.")

    # Keep only required columns (avoid leaking extra columns)
    df = df[FEATURES_ORDER + [TARGET_COL]].copy()

    # Basic cleanup: fill NA with 0 (mock thường không có NA, dataset thật có thể có)
    df = df.replace([np.inf, -np.inf], np.nan)
    df[FEATURES_ORDER] = df[FEATURES_ORDER].fillna(0)

    # Ensure numeric
    for c in FEATURES_ORDER + [TARGET_COL]:
        df[c] = pd.to_numeric(df[c], errors="coerce")
    df = df.dropna(subset=[TARGET_COL]).copy()
    df[FEATURES_ORDER] = df[FEATURES_ORDER].fillna(0)

    X = df[FEATURES_ORDER].values
    y = df[TARGET_COL].values

    # ======================
    # 3) Split train/test
    # ======================
    X_train, X_test, y_train, y_test = train_test_split(
        X, y,
        test_size=args.test_size,
        random_state=args.seed
    )

    # ======================
    # 4) Build & train
    # ======================
    pipe = build_model(args.model, args.use_scaler, args.seed)
    pipe.fit(X_train, y_train)

    # ======================
    # 5) Evaluate
    # ======================
    pred_train = pipe.predict(X_train)
    pred_test = pipe.predict(X_test)

    metrics = {
        "model": args.model,
        "use_scaler": bool(args.use_scaler),
        "data": args.data,
        "n_rows": int(len(df)),
        "train_size": int(len(X_train)),
        "test_size": int(len(X_test)),
        "train_metrics": compute_metrics(y_train, pred_train),
        "test_metrics": compute_metrics(y_test, pred_test),
        "trained_at": datetime.now().isoformat(timespec="seconds"),
        "features_count": len(FEATURES_ORDER),
    }

    print("\n===== TRAIN METRICS =====")
    for k, v in metrics["train_metrics"].items():
        print(f"{k}: {v:.6f}")

    print("\n===== TEST METRICS =====")
    for k, v in metrics["test_metrics"].items():
        print(f"{k}: {v:.6f}")

    # ======================
    # 6) Save artifacts
    # ======================
    model_path = os.path.join(args.artifacts, "model.pkl")
    joblib.dump(pipe, model_path)

    # Save scaler separately (optional but useful for non-pipeline usage)
    scaler_path = os.path.join(args.artifacts, "scaler.pkl")
    scaler = None
    if "scaler" in pipe.named_steps:
        scaler = pipe.named_steps["scaler"]
        joblib.dump(scaler, scaler_path)
    else:
        # if not using scaler, remove old scaler file if exists to avoid confusion
        if os.path.exists(scaler_path):
            os.remove(scaler_path)

    features_path = os.path.join(args.artifacts, "features.json")
    with open(features_path, "w", encoding="utf-8") as f:
        json.dump({"features_order": FEATURES_ORDER}, f, ensure_ascii=False, indent=2)

    metrics_path = os.path.join(args.artifacts, "metrics.json")
    with open(metrics_path, "w", encoding="utf-8") as f:
        json.dump(metrics, f, ensure_ascii=False, indent=2)

    # Save one example input from test set for quick API testing
    example = {feat: float(df.loc[df.index[0], feat]) for feat in FEATURES_ORDER}
    example_path = os.path.join(args.artifacts, "example_input.json")
    with open(example_path, "w", encoding="utf-8") as f:
        json.dump(example, f, ensure_ascii=False, indent=2)

    print("\n===== SAVED ARTIFACTS =====")
    print("Model   :", model_path)
    if scaler is not None:
        print("Scaler  :", scaler_path)
    print("Features:", features_path)
    print("Metrics :", metrics_path)
    print("Example :", example_path)
    print("\nDone.")


if __name__ == "__main__":
    main()
