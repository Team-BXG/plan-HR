from django.contrib.auth import authenticate
from django.utils import timezone
from datetime import timedelta
from .models import User, Role
from employees.services import EmployeeService
from attendance.services import AttendanceService
from leave.services import LeaveService


class AuthenticationService:
    """Service class for authentication operations"""
    
    def authenticate_user(self, username, password):
        """Authenticate user and return user with role"""
        try:
            user = authenticate(username=username, password=password)
            if user and user.is_active:
                # Get user role from User model or Role model
                role = getattr(user, 'role', 'Employee')
                if not role:
                    # Check role table
                    user_role = Role.objects.filter(user=user, is_active=True).first()
                    role = user_role.role if user_role else 'Employee'
                
                return {
                    'user': user,
                    'role': role,
                    'success': True
                }
            return {'success': False, 'message': 'Invalid credentials'}
        except Exception as e:
            return {'success': False, 'message': str(e)}
    
    def get_user_role(self, user):
        """Get user role from database"""
        try:
            # First check user.role field
            if hasattr(user, 'role') and user.role:
                return user.role
            
            # Then check role table
            user_role = Role.objects.filter(user=user, is_active=True).first()
            return user_role.role if user_role else 'Employee'
        except:
            return 'Employee'
    
    def change_password(self, user, old_password, new_password):
        """Change user password"""
        try:
            # Verify old password
            if not user.check_password(old_password):
                return {'success': False, 'message': 'Old password is incorrect'}
            
            # Validate new password (must be exactly 6 alphanumeric characters)
            if not new_password.isalnum() or len(new_password) != 6:
                return {'success': False, 'message': 'Password must be exactly 6 letters and/or numbers'}
            
            # Set new password
            user.set_password(new_password)
            user.save()
            
            return {'success': True, 'message': 'Password changed successfully'}
        except Exception as e:
            return {'success': False, 'message': str(e)}
    
    def get_dashboard_data(self, user):
        """Get dashboard data based on user role"""
        role = self.get_user_role(user)
        
        if role == 'Admin':
            return self._get_admin_dashboard()
        elif role == 'HR Manager':
            return self._get_hr_dashboard()
        elif role == 'Employee':
            return self._get_employee_dashboard(user)
        else:
            return {}
    
    def _get_admin_dashboard(self):
        """Get admin dashboard data"""
        employee_service = EmployeeService()
        attendance_service = AttendanceService()
        leave_service = LeaveService()
        
        total_employees = employee_service.get_total_employees_count()
        active_employees = employee_service.get_active_employees_count()
        total_departments = employee_service.get_total_departments_count()
        today_attendance = attendance_service.get_today_attendance_summary()
        pending_leaves = leave_service.get_pending_leaves_count()
        
        return {
            'role': 'Admin',
            'stats': {
                'total_employees': total_employees,
                'active_employees': active_employees,
                'total_departments': total_departments,
                'today_present': today_attendance.get('present', 0),
                'today_absent': today_attendance.get('absent', 0),
                'pending_leaves': pending_leaves
            }
        }
    
    def _get_hr_dashboard(self):
        """Get HR dashboard data"""
        employee_service = EmployeeService()
        attendance_service = AttendanceService()
        leave_service = LeaveService()
        
        total_employees = employee_service.get_total_employees_count()
        active_employees = employee_service.get_active_employees_count()
        today_attendance = attendance_service.get_today_attendance_summary()
        pending_leaves = leave_service.get_pending_leaves_count()
        
        return {
            'role': 'HR Manager',
            'stats': {
                'total_employees': total_employees,
                'active_employees': active_employees,
                'today_present': today_attendance.get('present', 0),
                'today_absent': today_attendance.get('absent', 0),
                'pending_leaves': pending_leaves
            }
        }
    
    def _get_employee_dashboard(self, user):
        """Get employee dashboard data"""
        attendance_service = AttendanceService()
        leave_service = LeaveService()
        
        # Get attendance summary for last 30 days
        thirty_days_ago = timezone.now().date() - timedelta(days=30)
        attendance_summary = attendance_service.get_employee_attendance_summary(
            user.employee_id, thirty_days_ago, timezone.now().date()
        )
        
        # Get available leave days
        available_leave = leave_service.get_available_leave_days(user.employee_id)
        
        return {
            'role': 'Employee',
            'stats': {
                'present_days': attendance_summary.get('present', 0),
                'absent_days': attendance_summary.get('absent', 0),
                'leave_days': attendance_summary.get('leave', 0),
                'available_leave_days': available_leave
            },
            'user_info': {
                'name': user.get_full_name() or user.username,
                'employee_id': user.employee_id,
                'department': user.department,
                'position': user.position
            }
        }
