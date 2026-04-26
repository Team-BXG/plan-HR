import requests
import json

def test_login():
    """Test login API"""
    url = "http://127.0.0.1:8000/api/auth/login/"
    data = {
        "username": "E001",
        "password": "admin9"
    }
    
    try:
        response = requests.post(url, json=data)
        print(f"Status: {response.status_code}")
        print(f"Response: {response.text}")
    except Exception as e:
        print(f"Error: {str(e)}")

if __name__ == "__main__":
    test_login()
