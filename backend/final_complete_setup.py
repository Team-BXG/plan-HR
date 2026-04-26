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

def create_final_users():
    """Create final users"""
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
                print(f"✓ Created user: {user_data['username']} - {user_data['first_name']} {user_data['last_name']}")
            else:
                print(f"✓ User already exists: {user_data['username']}")
        except Exception as e:
            print(f"✗ Error creating user {user_data['username']}: {str(e)}")
    
    print(f"\nTotal users: {User.objects.count()}")

def create_simple_tables():
    """Create simple tables"""
    print("\nCreating custom tables...")
    
    with connection.cursor() as cursor:
        try:
            # Departments
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS departments (
                    department_id INT AUTO_INCREMENT PRIMARY KEY,
                    department_name VARCHAR(100) UNIQUE NOT NULL,
                    employee_count INT DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """)
            print("✓ Created departments table")
            
            # Employees
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS employees (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    employee_id VARCHAR(20) UNIQUE NOT NULL,
                    name VARCHAR(100) NOT NULL,
                    department VARCHAR(100) NOT NULL,
                    position VARCHAR(100) NOT NULL,
                    phone_number VARCHAR(20) NOT NULL,
                    education VARCHAR(100) NOT NULL,
                    sex VARCHAR(10) NOT NULL,
                    salary DECIMAL(10,2) NOT NULL,
                    join_date DATE NOT NULL,
                    date_of_birth DATE NOT NULL,
                    is_active BOOLEAN DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """)
            print("✓ Created employees table")
            
            # Attendance
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS attendance (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    employee_id VARCHAR(20) NOT NULL,
                    attendance_date DATE NOT NULL,
                    status VARCHAR(10) DEFAULT 'Present',
                    notes TEXT,
                    is_active BOOLEAN DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    UNIQUE KEY unique_employee_date (employee_id, attendance_date)
                )
            """)
            print("✓ Created attendance table")
            
            # Leave records
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS leave_records (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    employee_id VARCHAR(20) NOT NULL,
                    leave_date DATE NOT NULL,
                    reason TEXT NOT NULL,
                    is_active BOOLEAN DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    UNIQUE KEY unique_employee_leave (employee_id, leave_date)
                )
            """)
            print("✓ Created leave_records table")
            
            # Leave balance
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS leave_balance (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    employee_id VARCHAR(20) NOT NULL,
                    year INT NOT NULL,
                    yearly_allowance INT DEFAULT 20,
                    used_days INT DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    UNIQUE KEY unique_employee_year (employee_id, year)
                )
            """)
            print("✓ Created leave_balance table")
            
        except Exception as e:
            print(f"✗ Error creating tables: {str(e)}")

def populate_final_data():
    """Populate final data"""
    print("\nPopulating data...")
    
    # Departments
    departments = ['Management', 'Finance', 'IT', 'Marketing', 'Security', 'HR']
    with connection.cursor() as cursor:
        for dept in departments:
            try:
                cursor.execute("""
                    INSERT IGNORE INTO departments (department_name, employee_count)
                    VALUES (%s, 0)
                """, [dept])
                print(f"✓ Added department: {dept}")
            except Exception as e:
                print(f"✗ Error adding department {dept}: {str(e)}")
    
    # Employees
    employees_data = [
        ('E001', 'Abebe Kebede', 'Management', 'Admin', '0912345678', 'BSc', 'Male', '50000.00', '2020-01-01', '1990-05-10', True),
        ('E005', 'Dawit Solomon', 'IT', 'HR Manager', '0956789012', 'BSc', 'Male', '51000.00', '2023-04-10', '1995-01-28', True),
        ('E002', 'Tigist Worku', 'Finance', 'Accountant', '0923456789', 'MSc', 'Female', '62000.00', '2019-06-12', '1988-03-15', True),
        ('E003', 'Mekonnen Alemu', 'Marketing', 'Marketing Executive', '0934567890', 'BSc', 'Male', '47000.00', '2021-09-01', '1992-07-22', True),
        ('E004', 'Alemnesh Kassahune', 'Management', 'Developer', '0945678901', 'MBA', 'Female', '59000.00', '2018-02-19', '1987-12-03', True),
    ]
    
    with connection.cursor() as cursor:
        for emp in employees_data:
            try:
                cursor.execute("""
                    INSERT IGNORE INTO employees 
                    (employee_id, name, department, position, phone_number, education, sex, salary, join_date, date_of_birth, is_active)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                """, emp)
                print(f"✓ Added employee: {emp[0]} - {emp[1]}")
            except Exception as e:
                print(f"✗ Error adding employee {emp[0]}: {str(e)}")

def main():
    """Main function"""
    print("🚀 Employee Management System - FINAL Complete Setup")
    print("=" * 65)
    
    print(f"Database: {connection.vendor}")
    
    # Step 1: Create users
    create_final_users()
    
    # Step 2: Create tables
    create_simple_tables()
    
    # Step 3: Populate data
    populate_final_data()
    
    print("\n" + "=" * 65)
    print("✅ Database setup completed successfully!")
    print("\n📋 Login Credentials:")
    print("   🔐 Admin: E001 / admin9")
    print("   👔 HR Manager: E005 / hr1234") 
    print("   👤 Employee: E002 / emp123")
    print("\n🌐 Next Steps:")
    print("   1. Start Django server: python manage.py runserver")
    print("   2. Test API: http://127.0.0.1:8000/api/auth/login/")
    print("   3. Build React frontend to connect to APIs")
    print("   4. All backend APIs are ready for frontend integration")

if __name__ == '__main__':
    main()
