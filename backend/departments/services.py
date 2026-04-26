from django.db import transaction
from django.shortcuts import get_object_or_404
from .models import Department
from employees.models import Employee


class DepartmentService:
    """Service class for department operations"""
    
    def get_all_departments(self):
        """Get all departments"""
        return Department.objects.all().order_by('department_name')
    
    def get_department_by_id(self, department_id):
        """Get department by ID"""
        try:
            return Department.objects.get(department_id=department_id)
        except Department.DoesNotExist:
            return None
    
    def get_department_by_name(self, department_name):
        """Get department by name"""
        try:
            return Department.objects.get(department_name__iexact=department_name)
        except Department.DoesNotExist:
            return None
    
    def create_department(self, department_name):
        """Create new department"""
        try:
            # Check if department already exists
            if self.get_department_by_name(department_name):
                return {
                    'success': False,
                    'message': 'Department with this name already exists'
                }
            
            # Create department
            department = Department.objects.create(
                department_name=department_name,
                employee_count=0
            )
            
            return {
                'success': True,
                'department': {
                    'department_id': department.department_id,
                    'department_name': department.department_name,
                    'employee_count': department.employee_count
                }
            }
        except Exception as e:
            return {
                'success': False,
                'message': str(e)
            }
    
    def update_department(self, department_id, new_name):
        """Update department name"""
        try:
            with transaction.atomic():
                department = self.get_department_by_id(department_id)
                if not department:
                    return {
                        'success': False,
                        'message': 'Department not found'
                    }
                
                old_name = department.department_name
                
                # Check if new name already exists
                existing_dept = self.get_department_by_name(new_name)
                if existing_dept and existing_dept.department_id != department_id:
                    return {
                        'success': False,
                        'message': 'Department with this name already exists'
                    }
                
                # Update department name
                department.department_name = new_name
                department.save()
                
                # Update all employees with this department
                Employee.objects.filter(department=old_name).update(department=new_name)
                
                return {
                    'success': True,
                    'department': {
                        'department_id': department.department_id,
                        'department_name': department.department_name,
                        'employee_count': department.employee_count
                    }
                }
        except Exception as e:
            return {
                'success': False,
                'message': str(e)
            }
    
    def delete_department(self, department_id):
        """Delete department"""
        try:
            with transaction.atomic():
                department = self.get_department_by_id(department_id)
                if not department:
                    return {
                        'success': False,
                        'message': 'Department not found'
                    }
                
                # Check if department has active employees
                active_employees_count = Employee.objects.filter(
                    department=department.department_name,
                    is_active=True
                ).count()
                
                if active_employees_count > 0:
                    return {
                        'success': False,
                        'message': f'Cannot delete department. It has {active_employees_count} active employees. Reassign them first.'
                    }
                
                # Delete department
                department.delete()
                
                return {
                    'success': True,
                    'message': 'Department deleted successfully'
                }
        except Exception as e:
            return {
                'success': False,
                'message': str(e)
            }
    
    def search_departments(self, search_term):
        """Search departments by name"""
        return Department.objects.filter(
            department_name__icontains=search_term
        ).order_by('department_name')
    
    def update_employee_count(self, department_name, change):
        """Update employee count for a department"""
        try:
            department = self.get_department_by_name(department_name)
            if department:
                department.employee_count += change
                if department.employee_count < 0:
                    department.employee_count = 0
                department.save()
        except:
            pass
    
    def get_total_departments_count(self):
        """Get total number of departments"""
        return Department.objects.count()
