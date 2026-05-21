from django.db import transaction
from django.shortcuts import get_object_or_404
from django.utils import timezone
from datetime import timedelta, date
from .models import Attendance, AttendanceSummary
from src.modules.employees.models import Employee
from src.modules.leave.models import LeaveRecord


class AttendanceService:
    """Service class for attendance operations"""
    
    def get_all_attendance(self):
        """Get all attendance records"""
        return Attendance.objects.filter(is_active=True).order_by('-attendance_date')
    
    def get_attendance_by_id(self, attendance_id):
        """Get attendance record by ID"""
        try:
            return Attendance.objects.get(attendance_id=attendance_id, is_active=True)
        except Attendance.DoesNotExist:
            return None
    
    def get_attendance_by_employee_and_date(self, employee_id, attendance_date):
        """Get attendance record by employee and date"""
        try:
            return Attendance.objects.get(
                employee_id=employee_id,
                attendance_date=attendance_date,
                is_active=True
            )
        except Attendance.DoesNotExist:
            return None
    
    def get_filtered_attendance(self, employee_id='', start_date='', end_date=''):
        """Get filtered attendance records"""
        queryset = Attendance.objects.filter(is_active=True)
        
        if employee_id:
            queryset = queryset.filter(employee_id=employee_id)
        
        if start_date:
            queryset = queryset.filter(attendance_date__gte=start_date)
        
        if end_date:
            queryset = queryset.filter(attendance_date__lte=end_date)
        
        return queryset.order_by('-attendance_date')
    
    def create_attendance(self, attendance_data, created_by):
        """Create attendance record"""
        try:
            with transaction.atomic():
                attendance = Attendance.objects.create(
                    employee_id=attendance_data['employee_id'],
                    attendance_date=attendance_data['attendance_date'],
                    status=attendance_data.get('status', 'Present'),
                    notes=attendance_data.get('notes', ''),
                    is_active=True
                )
                
                # Update daily summary
                self._update_daily_summary(attendance_data['attendance_date'])
                
                return {
                    'success': True,
                    'attendance': {
                        'attendance_id': attendance.attendance_id,
                        'employee_id': attendance.employee_id,
                        'attendance_date': attendance.attendance_date,
                        'status': attendance.status,
                        'notes': attendance.notes,
                        'is_active': attendance.is_active
                    }
                }
        except Exception as e:
            return {
                'success': False,
                'message': str(e)
            }
    
    def update_attendance(self, attendance_id, attendance_data, updated_by):
        """Update attendance record"""
        try:
            with transaction.atomic():
                attendance = self.get_attendance_by_id(attendance_id)
                if not attendance:
                    return {
                        'success': False,
                        'message': 'Attendance record not found'
                    }
                
                # Update fields
                for field, value in attendance_data.items():
                    if hasattr(attendance, field):
                        setattr(attendance, field, value)
                
                attendance.save()
                
                # Update daily summary
                self._update_daily_summary(attendance.attendance_date)
                
                return {
                    'success': True,
                    'attendance': {
                        'attendance_id': attendance.attendance_id,
                        'employee_id': attendance.employee_id,
                        'attendance_date': attendance.attendance_date,
                        'status': attendance.status,
                        'notes': attendance.notes,
                        'is_active': attendance.is_active
                    }
                }
        except Exception as e:
            return {
                'success': False,
                'message': str(e)
            }
    
    def punch_in(self, employee_id, punched_by):
        """Punch in employee for today"""
        try:
            today = timezone.now().date()
            
            # Check if already punched in today
            if self.get_attendance_by_employee_and_date(employee_id, today):
                return {
                    'success': False,
                    'message': 'Already punched in today'
                }
            
            # Check if on leave today
            if LeaveRecord.objects.filter(
                employee_id=employee_id,
                leave_date=today,
                is_active=True
            ).exists():
                return {
                    'success': False,
                    'message': 'Cannot punch in while on leave'
                }
            
            # Create attendance record
            attendance = Attendance.objects.create(
                employee_id=employee_id,
                attendance_date=today,
                status='Present',
                notes='Punched in',
                is_active=True
            )
            
            # Update daily summary
            self._update_daily_summary(today)
            
            return {
                'success': True,
                'attendance': {
                    'attendance_id': attendance.attendance_id,
                    'employee_id': attendance.employee_id,
                    'attendance_date': attendance.attendance_date,
                    'status': attendance.status,
                    'notes': attendance.notes,
                    'is_active': attendance.is_active
                }
            }
        except Exception as e:
            return {
                'success': False,
                'message': str(e)
            }
    
    def can_punch_in(self, employee_id, target_date):
        """Check if employee can punch in for given date"""
        # Check if already has attendance record
        if self.get_attendance_by_employee_and_date(employee_id, target_date):
            return False
        
        # Check if on leave
        if LeaveRecord.objects.filter(
            employee_id=employee_id,
            leave_date=target_date,
            is_active=True
        ).exists():
            return False
        
        return True
    
    def get_daily_attendance(self, employee_id, target_date):
        """Get daily attendance for employee"""
        return self.get_attendance_by_employee_and_date(employee_id, target_date)
    
    def generate_attendance_report(self, employee_id='', department='', start_date='', end_date=''):
        """Generate attendance report"""
        queryset = Attendance.objects.filter(is_active=True)
        
        if employee_id:
            queryset = queryset.filter(employee_id=employee_id)
        
        if department:
            queryset = queryset.filter(employee__department=department)
        
        if start_date:
            queryset = queryset.filter(attendance_date__gte=start_date)
        
        if end_date:
            queryset = queryset.filter(attendance_date__lte=end_date)
        
        return queryset.order_by('-attendance_date')
    
    def get_report_summary(self, report_data):
        """Get summary statistics for report data"""
        summary = {
            'total_records': report_data.count(),
            'present_days': report_data.filter(status='Present').count(),
            'absent_days': report_data.filter(status='Absent').count(),
            'leave_days': report_data.filter(status='Leave').count(),
        }
        return summary
    
    def get_attendance_summary(self, employee_id='', from_date='', to_date=''):
        """Get attendance summary for date range"""
        queryset = Attendance.objects.filter(is_active=True)
        
        if employee_id:
            queryset = queryset.filter(employee_id=employee_id)
        
        if from_date:
            queryset = queryset.filter(attendance_date__gte=from_date)
        
        if to_date:
            queryset = queryset.filter(attendance_date__lte=to_date)
        
        return {
            'present': queryset.filter(status='Present').count(),
            'absent': queryset.filter(status='Absent').count(),
            'leave': queryset.filter(status='Leave').count(),
            'total': queryset.count()
        }
    
    def get_employee_attendance_summary(self, employee_id, from_date, to_date):
        """Get attendance summary for specific employee"""
        return self.get_attendance_summary(employee_id, from_date, to_date)
    
    def get_today_attendance_summary(self):
        """Get today's attendance summary"""
        today = timezone.now().date()
        return self.get_attendance_summary('', today, today)
    
    def _update_daily_summary(self, target_date):
        """Update daily attendance summary"""
        try:
            with transaction.atomic():
                # Get all attendance records for the date
                attendance_records = Attendance.objects.filter(
                    attendance_date=target_date,
                    is_active=True
                )
                
                # Calculate counts
                total_employees = Employee.objects.filter(is_active=True).count()
                present_count = attendance_records.filter(status='Present').count()
                absent_count = attendance_records.filter(status='Absent').count()
                leave_count = attendance_records.filter(status='Leave').count()
                
                # Update or create summary
                summary, created = AttendanceSummary.objects.get_or_create(
                    date=target_date,
                    defaults={
                        'total_employees': total_employees,
                        'present_count': present_count,
                        'absent_count': absent_count,
                        'leave_count': leave_count
                    }
                )
                
                if not created:
                    summary.total_employees = total_employees
                    summary.present_count = present_count
                    summary.absent_count = absent_count
                    summary.leave_count = leave_count
                    summary.save()
        except Exception:
            pass  # Silently handle errors in summary updates
