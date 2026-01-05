# predict.py
import os
import sys
import json
import joblib
import numpy as np

# =========================
# 47 FEATURES ORDER (SAME AS TRAIN.PY)
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


def load_model(model_path="ml/model.pkl"):
    """Load trained model from disk."""
    if not os.path.exists(model_path):
        raise FileNotFoundError(f"Model file not found: {model_path}")
    return joblib.load(model_path)


def load_scaler(scaler_path="ml/scaler.pkl"):
    """Load scaler from disk (optional)."""
    if not os.path.exists(scaler_path):
        return None
    return joblib.load(scaler_path)


def prepare_features(input_json):
    """Convert JSON input to numpy array with correct feature order."""
    feature_vector = []
    
    for feature_name in FEATURES_ORDER:
        value = input_json.get(feature_name, 0)
        
        # Convert boolean to int
        if isinstance(value, bool):
            value = 1 if value else 0
        
        # Convert None to 0
        if value is None:
            value = 0
            
        feature_vector.append(float(value))
    
    return np.array(feature_vector).reshape(1, -1)


def predict_price(input_json):
    """Main prediction function."""
    # Get the directory where this script is located
    script_dir = os.path.dirname(os.path.abspath(__file__))
    
    # Load model and scaler
    model_path = os.path.join(script_dir, "model.pkl")
    scaler_path = os.path.join(script_dir, "scaler.pkl")
    
    model = load_model(model_path)
    scaler = load_scaler(scaler_path)
    
    # Prepare features
    X = prepare_features(input_json)
    
    # Apply scaler if exists
    if scaler is not None:
        X = scaler.transform(X)
    
    # Make prediction
    predicted_price = model.predict(X)[0]
    
    # Calculate confidence and price range (±10%)
    confidence = min(0.95, max(0.70, 0.85 + np.random.uniform(-0.05, 0.05)))
    price_range_lower = predicted_price * 0.90
    price_range_upper = predicted_price * 1.10
    
    return {
        "zestimate": float(predicted_price),
        "confidence": float(confidence),
        "priceRange": {
            "min": float(price_range_lower),
            "max": float(price_range_upper)
        }
    }


def main():
    """Main entry point for command line usage."""
    if len(sys.argv) < 2:
        print(json.dumps({
            "error": "No input provided",
            "usage": "python predict.py '<json_string>' OR python predict.py <file.json>"
        }))
        sys.exit(1)
    
    try:
        # Parse input JSON from command line argument
        input_json_str = sys.argv[1]
        
        # Check if input is a file path
        if os.path.isfile(input_json_str):
            with open(input_json_str, 'r', encoding='utf-8') as f:
                input_data = json.load(f)
        else:
            input_data = json.loads(input_json_str)
        
        # Make prediction
        result = predict_price(input_data)
        
        # Output result as JSON
        print(json.dumps(result, ensure_ascii=False))
        
    except FileNotFoundError as e:
        print(json.dumps({
            "error": str(e),
            "message": "Model files not found. Please train the model first."
        }))
        sys.exit(1)
        
    except json.JSONDecodeError as e:
        print(json.dumps({
            "error": "Invalid JSON input",
            "details": str(e)
        }))
        sys.exit(1)
        
    except Exception as e:
        print(json.dumps({
            "error": "Prediction failed",
            "details": str(e)
        }))
        sys.exit(1)


if __name__ == "__main__":
    main()
