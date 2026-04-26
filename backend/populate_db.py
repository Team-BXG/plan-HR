import os
import sys
import django

# Setup Django
sys.path.append(os.path.dirname(os.path.abspath(__file__)))
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'employee_management.settings')
django.setup()

from django.contrib.auth import get_user_model
from employees.models import Employee
from departments.models import Department
from leave.models import LeaveBalance
from datetime import date

User = get_user_model()

def populate_database():
    """Populate database with employee data"""
    
    print("Starting database population...")
    
    # Create departments first
    departments_data = [
        'Management', 'Finance', 'IT', 'Marketing', 'Security', 'HR'
    ]
    
    for dept_name in departments_data:
        dept, created = Department.objects.get_or_create(
            department_name=dept_name,
            defaults={'employee_count': 0}
        )
        if created:
            print(f"Created department: {dept_name}")
    
    # Employee data from your list
    employees_data = [
        {
            'employee_id': 'E001',
            'name': 'Abebe Kebede',
            'education': 'BSc',
            'department': 'Management',
            'position': 'Admin',
            'sex': 'Male',
            'date_of_birth': '1990-05-10',
            'join_date': '2020-01-01',
            'salary': '50000.00',
            'phone_number': '0912345678',
            'password': 'admin9',
            'role': 'Admin'
        },
        {
            'employee_id': 'E0019',
            'name': 'kebe Abuch',
            'education': 'High School',
            'department': 'Management',
            'position': 'Cleaner',
            'sex': 'Female',
            'date_of_birth': '2026-01-04',
            'join_date': '2026-01-04',
            'salary': '67880.00',
            'phone_number': '0978652468',
            'password': 'emp123',
            'role': 'Employee'
        },
        {
            'employee_id': 'E002',
            'name': 'Tigist Worku',
            'education': 'MSc',
            'department': 'Finance',
            'position': 'Accountant',
            'sex': 'Female',
            'date_of_birth': '1988-03-15',
            'join_date': '2019-06-12',
            'salary': '62000.00',
            'phone_number': '0923456789',
            'password': 'emp123',
            'role': 'Employee'
        },
        {
            'employee_id': 'E0020',
            'name': 'yuiiiiiii iiiiiiiiii',
            'education': 'High School',
            'department': 'IT',
            'position': 'Cleaner',
            'sex': 'Female',
            'date_of_birth': '2026-01-04',
            'join_date': '2026-01-04',
            'salary': '6786.00',
            'phone_number': '0997654667',
            'password': 'emp123',
            'role': 'Employee'
        },
        {
            'employee_id': 'E0021',
            'name': 'yene deseee',
            'education': 'High School',
            'department': 'Management',
            'position': 'Developer',
            'sex': 'Female',
            'date_of_birth': '2026-01-05',
            'join_date': '2026-01-05',
            'salary': '456666.00',
            'phone_number': '0906765433',
            'password': 'emp123',
            'role': 'Employee'
        },
        {
            'employee_id': 'E003',
            'name': 'Mekonnen Alemu',
            'education': 'BSc',
            'department': 'Marketing',
            'position': 'Marketing Executive',
            'sex': 'Male',
            'date_of_birth': '1992-07-22',
            'join_date': '2021-09-01',
            'salary': '47000.00',
            'phone_number': '0934567890',
            'password': 'emp123',
            'role': 'Employee'
        },
        {
            'employee_id': 'E004',
            'name': 'Alemnesh Kassahune',
            'education': 'MBA',
            'department': 'Management',
            'position': 'Developer',
            'sex': 'Female',
            'date_of_birth': '1987-12-03',
            'join_date': '2018-02-19',
            'salary': '59000.00',
            'phone_number': '0945678901',
            'password': 'emp123',
            'role': 'Employee'
        },
        {
            'employee_id': 'E005',
            'name': 'Dawit Solomon',
            'education': 'BSc',
            'department': 'IT',
            'position': 'HR Manager',
            'sex': 'Male',
            'date_of_birth': '1995-01-28',
            'join_date': '2023-04-10',
            'salary': '51000.00',
            'phone_number': '0956789012',
            'password': 'hr1234',
            'role': 'HR Manager'
        },
        {
            'employee_id': 'E006',
            'name': 'Tsion Habtu',
            'education': 'Bachelor\'s',
            'department': 'IT',
            'position': 'Manager',
            'sex': 'Female',
            'date_of_birth': '2025-05-19',
            'join_date': '2025-05-19',
            'salary': '52000.00',
            'phone_number': '0987654323',
            'password': 'emp123',
            'role': 'Employee'
        },
        {
            'employee_id': 'E007',
            'name': 'Hermela Belay',
            'education': 'Bachelor\'s',
            'department': 'Security',
            'position': 'Developer',
            'sex': 'Female',
            'date_of_birth': '2025-05-18',
            'join_date': '2025-05-18',
            'salary': '56000.00',
            'phone_number': '0989898989',
            'password': 'emp123',
            'role': 'Employee'
        },
        {
            'employee_id': 'E008',
            'name': 'Deborah Habtu',
            'education': 'Master\'s',
            'department': 'IT',
            'position': 'Analyst',
            'sex': 'Female',
            'date_of_birth': '2025-05-18',
            'join_date': '2025-05-18',
            'salary': '45000.00',
            'phone_number': '0945678989',
            'password': 'emp123',
            'role': 'Employee'
        },
        {
            'employee_id': 'E009',
            'name': 'beza dememe',
            'education': 'Bachelor\'s',
            'department': 'IT',
            'position': 'Analyst',
            'sex': 'Female',
            'date_of_birth': '2025-05-18',
            'join_date': '2025-05-18',
            'salary': '45000.00',
            'phone_number': '0967567889',
            'password': 'emp123',
            'role': 'Employee'
        },
        {
            'employee_id': 'E011',
            'name': 'Abraham Kebede',
            'education': 'BSc',
            'department': 'Management',
            'position': 'Developer',
            'sex': 'Male',
            'date_of_birth': '1990-05-10',
            'join_date': '2020-01-01',
            'salary': '50000.00',
            'phone_number': '0912345667',
            'password': 'emp123',
            'role': 'Employee'
        },
        {
            'employee_id': 'E012',
            'name': 'Bemnet Worku',
            'education': 'Bachelor\'s',
            'department': 'Finance',
            'position': 'Intern',
            'sex': 'Female',
            'date_of_birth': '2025-05-20',
            'join_date': '2025-05-20',
            'salary': '4500.00',
            'phone_number': '0908765455',
            'password': 'emp123',
            'role': 'Employee',
            'is_active': False  # Inactive as per your data
        },
        {
            'employee_id': 'E013',
            'name': 'Mahilet Tesfaye',
            'education': 'Bachelor\'s',
            'department': 'Management',
            'position': 'Developer',
            'sex': 'Female',
            'date_of_birth': '2025-05-20',
            'join_date': '2025-05-20',
            'salary': '340000.00',
            'phone_number': '0906543234',
            'password': 'emp123',
            'role': 'Employee'
        },
        {
            'employee_id': 'E014',
            'name': 'Genet Tesafye',
            'education': '',
            'department': 'IT',
            'position': 'Manager',
            'sex': 'Female',
            'date_of_birth': '2025-05-21',
            'join_date': '2025-05-21',
            'salary': '567800.00',
            'phone_number': '0967564534',
            'password': 'emp123',
            'role': 'Employee'
        }
    ]
    
    # Create users and employees
    for emp_data in employees_data:
        try:
            # Create User
            user, created = User.objects.get_or_create(
                username=emp_data['employee_id'],
                defaults={
                    'email': f"{emp_data['employee_id']}@company.com",
                    'employee_id': emp_data['employee_id'],
                    'role': emp_data['role'],
                    'phone_number': emp_data['phone_number'],
                    'department': emp_data['department'],
                    'position': emp_data['position'],
                    'education': emp_data['education'],
                    'sex': emp_data['sex'],
                    'salary': emp_data['salary'],
                    'join_date': emp_data['join_date'],
                    'date_of_birth': emp_data['date_of_birth'],
                    'is_active': emp_data.get('is_active', True)
                }
            )
            
            if created:
                user.set_password(emp_data['password'])
                user.save()
                print(f"Created user: {emp_data['employee_id']} - {emp_data['name']}")
            else:
                print(f"User already exists: {emp_data['employee_id']}")
            
            # Create Employee record
            employee, emp_created = Employee.objects.get_or_create(
                employee_id=emp_data['employee_id'],
                defaults={
                    'user': user,
                    'name': emp_data['name'],
                    'department': emp_data['department'],
                    'position': emp_data['position'],
                    'phone_number': emp_data['phone_number'],
                    'education': emp_data['education'],
                    'sex': emp_data['sex'],
                    'salary': emp_data['salary'],
                    'join_date': emp_data['join_date'],
                    'date_of_birth': emp_data['date_of_birth'],
                    'is_active': emp_data.get('is_active', True)
                }
            )
            
            if emp_created:
                print(f"Created employee: {emp_data['employee_id']} - {emp_data['name']}")
            
            # Create leave balance
            current_year = date.today().year
            leave_balance, leave_created = LeaveBalance.objects.get_or_create(
                employee=employee,
                year=current_year,
                defaults={
                    'yearly_allowance': 20,
                    'used_days': 0
                }
            )
            
            if leave_created:
                print(f"Created leave balance for: {emp_data['employee_id']}")
            
            # Update department count
            if emp_created and emp_data.get('is_active', True):
                department = Department.objects.get(department_name=emp_data['department'])
                department.employee_count += 1
                department.save()
                
        except Exception as e:
            print(f"Error creating employee {emp_data['employee_id']}: {str(e)}")
    
    print("Database population completed!")
    
    # Print summary
    print(f"\nSummary:")
    print(f"Users created: {User.objects.count()}")
    print(f"Employees created: {Employee.objects.count()}")
    print(f"Departments: {Department.objects.count()}")
    print(f"Leave balances: {LeaveBalance.objects.count()}")

if __name__ == '__main__':
    populate_database()
