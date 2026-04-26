from django.db import transaction
from django.shortcuts import get_object_or_404
from django.utils import timezone
from datetime import timedelta
from django.contrib.auth import get_user_model
from .models import Employee, InactiveEmployee
from departments.models import Department
from departments.services import DepartmentService

User = get_user_model()


class EmployeeService:
    """Service class for employee operations"""
    
    def get_all_employees(self):
        """Get all active employees"""
        return Employee.objects.filter(is_active=True).order_by('name')
    
    def get_employee_by_id(self, employee_id):
        """Get employee by ID"""
        try:
            return Employee.objects.get(employee_id=employee_id, is_active=True)
        except Employee.DoesNotExist:
            return None
    
    def get_employee_by_user(self, user):
        """Get employee by user"""
        try:
            return Employee.objects.get(user=user, is_active=True)
        except Employee.DoesNotExist:
            return None
    
    def create_employee(self, employee_data, created_by):
        """Create new employee"""
        try:
            with transaction.atomic():
                # Create User account
                user = User.objects.create_user(
                    username=employee_data['employee_id'],
                    email=f"{employee_data['employee_id']}@company.com",
                    password=employee_data['password'],
                    employee_id=employee_data['employee_id'],
                    role='Employee',  # Default role
                    phone_number=employee_data['phone_number'],
                    department=employee_data['department'],
                    position=employee_data['position'],
                    education=employee_data['education'],
                    sex=employee_data['sex'],
                    salary=employee_data['salary'],
                    join_date=employee_data['join_date'],
                    date_of_birth=employee_data['date_of_birth'],
                    is_active=True
                )
                
                # Create Employee record
                employee = Employee.objects.create(
                    user=user,
                    employee_id=employee_data['employee_id'],
                    name=employee_data['name'],
                    department=employee_data['department'],
                    position=employee_data['position'],
                    phone_number=employee_data['phone_number'],
                    education=employee_data['education'],
                    sex=employee_data['sex'],
                    salary=employee_data['salary'],
                    join_date=employee_data['join_date'],
                    date_of_birth=employee_data['date_of_birth'],
                    is_active=True
                )
                
                # Update department count
                dept_service = DepartmentService()
                dept_service.update_employee_count(employee_data['department'], 1)
                
                return {
                    'success': True,
                    'employee': {
                        'employee_id': employee.employee_id,
                        'name': employee.name,
                        'department': employee.department,
                        'position': employee.position,
                        'phone_number': employee.phone_number,
                        'education': employee.education,
                        'sex': employee.sex,
                        'salary': str(employee.salary),
                        'join_date': employee.join_date,
                        'date_of_birth': employee.date_of_birth,
                        'is_active': employee.is_active
                    }
                }
        except Exception as e:
            return {
                'success': False,
                'message': str(e)
            }
    
    def update_employee(self, employee_id, employee_data, updated_by):
        """Update employee"""
        try:
            with transaction.atomic():
                employee = self.get_employee_by_id(employee_id)
                if not employee:
                    return {
                        'success': False,
                        'message': 'Employee not found'
                    }
                
                old_department = employee.department
                
                # Update employee fields
                for field, value in employee_data.items():
                    if hasattr(employee, field):
                        setattr(employee, field, value)
                
                employee.save()
                
                # Update user fields
                user = employee.user
                for field, value in employee_data.items():
                    if hasattr(user, field):
                        setattr(user, field, value)
                user.save()
                
                # Update department count if department changed
                if old_department != employee_data.get('department', old_department):
                    dept_service = DepartmentService()
                    dept_service.update_employee_count(old_department, -1)
                    dept_service.update_employee_count(employee_data['department'], 1)
                
                return {
                    'success': True,
                    'employee': {
                        'employee_id': employee.employee_id,
                        'name': employee.name,
                        'department': employee.department,
                        'position': employee.position,
                        'phone_number': employee.phone_number,
                        'education': employee.education,
                        'sex': employee.sex,
                        'salary': str(employee.salary),
                        'join_date': employee.join_date,
                        'date_of_birth': employee.date_of_birth,
                        'is_active': employee.is_active
                    }
                }
        except Exception as e:
            return {
                'success': False,
                'message': str(e)
            }
    
    def deactivate_employee(self, employee_id, deactivated_by):
        """Deactivate employee"""
        try:
            with transaction.atomic():
                employee = self.get_employee_by_id(employee_id)
                if not employee:
                    return {
                        'success': False,
                        'message': 'Employee not found'
                    }
                
                # Check if user is Admin or HR Manager
                user_role = getattr(deactivated_by, 'role', 'Employee')
                if user_role not in ['Admin', 'HR Manager']:
                    return {
                        'success': False,
                        'message': 'Permission denied'
                    }
                
                # Check if employee is Admin or HR Manager
                employee_role = getattr(employee.user, 'role', 'Employee')
                if employee_role in ['Admin', 'HR Manager']:
                    return {
                        'success': False,
                        'message': 'Cannot deactivate Admin or HR Manager accounts'
                    }
                
                # Deactivate employee
                employee.is_active = False
                employee.save()
                
                # Deactivate user account
                employee.user.is_active = False
                employee.user.save()
                
                # Create inactive record
                InactiveEmployee.objects.create(
                    employee=employee,
                    deactivated_by=deactivated_by
                )
                
                # Update department count
                dept_service = DepartmentService()
                dept_service.update_employee_count(employee.department, -1)
                
                return {
                    'success': True,
                    'message': 'Employee deactivated successfully'
                }
        except Exception as e:
            return {
                'success': False,
                'message': str(e)
            }
    
    def reactivate_employee(self, employee_id, reactivated_by):
        """Reactivate employee"""
        try:
            with transaction.atomic():
                # Get inactive employee
                try:
                    inactive_employee = InactiveEmployee.objects.get(employee__employee_id=employee_id)
                    employee = inactive_employee.employee
                except InactiveEmployee.DoesNotExist:
                    return {
                        'success': False,
                        'message': 'Inactive employee not found'
                    }
                
                # Reactivate employee
                employee.is_active = True
                employee.save()
                
                # Reactivate user account
                employee.user.is_active = True
                employee.user.save()
                
                # Delete inactive record
                inactive_employee.delete()
                
                # Update department count
                dept_service = DepartmentService()
                dept_service.update_employee_count(employee.department, 1)
                
                return {
                    'success': True,
                    'message': 'Employee reactivated successfully'
                }
        except Exception as e:
            return {
                'success': False,
                'message': str(e)
            }
    
    def get_inactive_employees(self):
        """Get all inactive employees"""
        return Employee.objects.filter(is_active=False).order_by('-updated_at')
    
    def search_employees(self, search_term):
        """Search employees by ID or name"""
        return Employee.objects.filter(
            is_active=True
        ).filter(
            models.Q(employee_id__icontains=search_term) |
            models.Q(name__icontains=search_term)
        ).order_by('name')
    
    def advanced_search(self, search_criteria):
        """Advanced employee search"""
        queryset = Employee.objects.filter(is_active=True)
        
        if search_criteria.get('employee_id'):
            queryset = queryset.filter(employee_id__icontains=search_criteria['employee_id'])
        
        if search_criteria.get('name'):
            queryset = queryset.filter(name__icontains=search_criteria['name'])
        
        if search_criteria.get('department'):
            queryset = queryset.filter(department=search_criteria['department'])
        
        if search_criteria.get('position'):
            queryset = queryset.filter(position=search_criteria['position'])
        
        if search_criteria.get('sex'):
            queryset = queryset.filter(sex=search_criteria['sex'])
        
        if search_criteria.get('education'):
            queryset = queryset.filter(education=search_criteria['education'])
        
        if search_criteria.get('salary_min'):
            queryset = queryset.filter(salary__gte=search_criteria['salary_min'])
        
        if search_criteria.get('salary_max'):
            queryset = queryset.filter(salary__lte=search_criteria['salary_max'])
        
        if search_criteria.get('join_date_from'):
            queryset = queryset.filter(join_date__gte=search_criteria['join_date_from'])
        
        if search_criteria.get('join_date_to'):
            queryset = queryset.filter(join_date__lte=search_criteria['join_date_to'])
        
        if search_criteria.get('seniority'):
            cutoff_date = timezone.now().date()
            seniority = search_criteria['seniority']
            if seniority == '>1 Year':
                cutoff_date = cutoff_date - timedelta(days=365)
            elif seniority == '>3 Years':
                cutoff_date = cutoff_date - timedelta(days=365*3)
            elif seniority == '>5 Years':
                cutoff_date = cutoff_date - timedelta(days=365*5)
            elif seniority == '>10 Years':
                cutoff_date = cutoff_date - timedelta(days=365*10)
            
            queryset = queryset.filter(join_date__lte=cutoff_date)
        
        return queryset.order_by('name')
    
    def filter_employees(self, filter_criteria):
        """Filter employees with advanced options"""
        queryset = Employee.objects.filter(is_active=True)
        
        if filter_criteria.get('department') and filter_criteria['department'] != 'All':
            queryset = queryset.filter(department=filter_criteria['department'])
        
        if filter_criteria.get('position') and filter_criteria['position'] != 'All':
            queryset = queryset.filter(position=filter_criteria['position'])
        
        if filter_criteria.get('gender') and filter_criteria['gender'] != 'All':
            queryset = queryset.filter(sex=filter_criteria['gender'])
        
        if filter_criteria.get('seniority') and filter_criteria['seniority'] != 'All':
            cutoff_date = timezone.now().date()
            seniority = filter_criteria['seniority']
            if seniority == '>1 Year':
                cutoff_date = cutoff_date - timedelta(days=365)
            elif seniority == '>3 Years':
                cutoff_date = cutoff_date - timedelta(days=365*3)
            elif seniority == '>5 Years':
                cutoff_date = cutoff_date - timedelta(days=365*5)
            
            queryset = queryset.filter(join_date__lte=cutoff_date)
        
        if filter_criteria.get('salary_range') and filter_criteria['salary_range'] != 'All':
            salary_range = filter_criteria['salary_range']
            if salary_range == '<10,000':
                queryset = queryset.filter(salary__lt=10000)
            elif salary_range == '10,000-30,000':
                queryset = queryset.filter(salary__gte=10000, salary__lte=30000)
            elif salary_range == '30,000-50,000':
                queryset = queryset.filter(salary__gte=30000, salary__lte=50000)
            elif salary_range == '>50,000':
                queryset = queryset.filter(salary__gt=50000)
        
        return queryset.order_by('name')
    
    def get_employee_master_detail(self, employee_id):
        """Get complete employee details"""
        employee = self.get_employee_by_id(employee_id)
        if not employee:
            return None
        
        return {
            'employee_id': employee.employee_id,
            'name': employee.name,
            'department': employee.department,
            'position': employee.position,
            'phone_number': employee.phone_number,
            'education': employee.education,
            'sex': employee.sex,
            'salary': str(employee.salary),
            'join_date': employee.join_date,
            'date_of_birth': employee.date_of_birth,
            'is_active': employee.is_active,
            'created_at': employee.created_at,
            'updated_at': employee.updated_at
        }
    
    def get_total_employees_count(self):
        """Get total number of active employees"""
        return Employee.objects.filter(is_active=True).count()
    
    def get_active_employees_count(self):
        """Get number of active employees"""
        return Employee.objects.filter(is_active=True).count()
    
    def get_total_departments_count(self):
        """Get total number of departments"""
        return Department.objects.count()
