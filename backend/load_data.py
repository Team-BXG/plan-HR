import os
import django
from datetime import datetime


def run():
    from employees.models import Employee
    from departments.models import Department
    from authentication.models import User
    from attendance.models import Attendance
    from leave.models import LeaveRecord, LeaveBalance
    from django.contrib.auth.hashers import make_password

    print("Flushing database...")
    Employee.objects.all().delete()
    Department.objects.all().delete()
    User.objects.all().delete()

    print("Adding Departments...")
    depts = [
        (1, 'HR'),
        (2, 'Marketing'),
        (3, 'Finance'),
        (4, 'IT'),
        (8, 'Administration')
    ]
    for d in depts:
        Department.objects.get_or_create(department_name=d[1], defaults={'employee_count': 0})

    print("Adding Users and Employees...")
    emp_data = [
        ('E001', 'Abebe Kebede', 'BSc', 'Management', 'Male', '1990-05-10', '2020-01-01', 50000.00, 'Admin', '0912345678', 'admin9', 'Admin'),
        ('E0019', 'kebe Abuch', 'High School', 'Management', 'Female', '2026-01-04', '2026-01-04', 67880.00, 'Cleaner', '0978652468', 'emp123', 'Employee'),
        ('E002', 'Tigist Worku', 'MSc', 'Finance', 'Female', '1988-03-15', '2019-06-12', 62000.00, 'Accountant', '0923456789', 'emp123', 'Employee'),
        ('E0020', 'yuiiiiiii iiiiiiiiii', 'High School', 'IT', 'Female', '2026-01-04', '2026-01-04', 6786.00, 'Cleaner', '0997654667', 'emp123', 'Employee'),
        ('E0021', 'yene deseee', 'High School', 'Management', 'Female', '2026-01-05', '2026-01-05', 456666.00, 'Developer', '0906765433', 'emp123', 'Employee'),
        ('E003', 'Mekonnen Alemu', 'BSc', 'Marketing', 'Male', '1992-07-22', '2021-09-01', 47000.00, 'Marketing Executive', '0934567890', 'emp123', 'Employee'),
        ('E004', 'Alemnesh Kassahune', 'MBA', 'Management', 'Female', '1987-12-03', '2018-02-19', 59000.00, 'Developer', '0945678901', 'emp123', 'Employee'),
        ('E005', 'Dawit Solomon', 'BSc', 'IT', 'Male', '1995-01-28', '2023-04-10', 51000.00, 'HR Manager', '0956789012', 'hr1234', 'HR Manager'),
        ('E006', 'Tsion Habtu', 'Bachelor\'s', 'IT', 'Female', '2025-05-19', '2025-05-19', 52000.00, 'Manager', '0987654323', 'emp123', 'Employee'),
        ('E007', 'Hermela Belay', 'Bachelor\'s', 'Security', 'Female', '2025-05-18', '2025-05-18', 56000.00, 'Developer', '0989898989', 'emp123', 'Employee'),
        ('E008', 'Deborah Habtu', 'Master\'s', 'IT', 'Female', '2025-05-18', '2025-05-18', 45000.00, 'Analyst', '0945678989', 'emp123', 'Employee'),
        ('E009', 'beza dememe', 'Bachelor\'s', 'IT', 'Female', '2025-05-18', '2025-05-18', 45000.00, 'Analyst', '0967567889', 'emp123', 'Employee'),
        ('E011', 'Abraham Kebede', 'BSc', 'Management', 'Male', '1990-05-10', '2020-01-01', 50000.00, 'Developer', '0912345667', 'emp123', 'Employee'),
        ('E012', 'Bemnet Worku', 'Bachelor\'s', 'Finance', 'Female', '2025-05-20', '2025-05-20', 4500.00, 'Intern', '0908765455', 'emp123', 'Employee'),
        ('E013', 'Mahilet Tesfaye', 'Bachelor\'s', 'Management', 'Female', '2025-05-20', '2025-05-20', 340000.00, 'Developer', '0906543234', 'emp123', 'Employee'),
        ('E014', 'Genet Tesafye', 'High School', 'IT', 'Female', '2025-05-21', '2025-05-21', 567800.00, 'Manager', '0967564534', 'emp123', 'Employee'),
    ]

    for data in emp_data:
        emp_id, name, edu, dept, sex, dob, join, salary, pos, phone, raw_pw, role = data
        
        # Django uses User object primarily
        user, created = User.objects.get_or_create(username=emp_id)
        user.set_password(raw_pw)
        user.role = role
        # Custom user model config
        user.employee_id = emp_id
        user.save()

        try:
            Employee.objects.get_or_create(
                user=user,
                employee_id=emp_id,
                name=name,
                department=dept,
                position=pos,
                phone_number=phone,
                education=edu,
                sex=sex,
                salary=salary,
                join_date=datetime.strptime(join, '%Y-%m-%d').date(),
                date_of_birth=datetime.strptime(dob, '%Y-%m-%d').date(),
                is_active=True
            )
        except Exception as e:
            print(f"Skipping redundant employee mapping: {e}")

    print("Adding Leave Balance...")
    # Initialize leave balances
    for e in Employee.objects.all():
        LeaveBalance.objects.get_or_create(employee=e, year=2025, defaults={'yearly_allowance': 20, 'used_days': 0})
        
    print("Re-counting departments...")
    for d in Department.objects.all():
        d.employee_count = Employee.objects.filter(department=d.department_name, is_active=True).count()
        d.save()

    print("Success: Database loaded heavily with original data.")

if __name__ == "__main__":
    run()
