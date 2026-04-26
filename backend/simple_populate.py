import os
import sys
import django

# Setup Django
sys.path.append(os.path.dirname(os.path.abspath(__file__)))
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'employee_management.settings')
django.setup()

from django.contrib.auth import get_user_model
from django.db import connection

User = get_user_model()

def create_simple_users():
    """Create simple users for testing"""
    print("Creating users...")
    
    users_data = [
        {
            'username': 'E001',
            'email': 'abebe@company.com',
            'password': 'admin9',
            'first_name': 'Abebe',
            'last_name': 'Kebede',
            'is_staff': True,
            'is_superuser': True,
        },
        {
            'username': 'E005',
            'email': 'dawit@company.com',
            'password': 'hr1234',
            'first_name': 'Dawit',
            'last_name': 'Solomon',
            'is_staff': True,
            'is_superuser': False,
        },
        {
            'username': 'E002',
            'email': 'tigist@company.com',
            'password': 'emp123',
            'first_name': 'Tigist',
            'last_name': 'Worku',
            'is_staff': False,
            'is_superuser': False,
        },
    ]
    
    for user_data in users_data:
        try:
            user, created = User.objects.get_or_create(
                username=user_data['username'],
                defaults=user_data
            )
            if created:
                user.set_password(user_data['password'])
                user.save()
                print(f"Created user: {user_data['username']} - {user_data['first_name']} {user_data['last_name']}")
            else:
                print(f"User already exists: {user_data['username']}")
        except Exception as e:
            print(f"Error creating user {user_data['username']}: {str(e)}")
    
    print(f"Total users: {User.objects.count()}")

if __name__ == '__main__':
    create_simple_users()
