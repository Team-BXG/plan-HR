from rest_framework import serializers
from .models import Attendance, AttendanceSummary
from employees.models import Employee


class AttendanceSerializer(serializers.ModelSerializer):
    """Serializer for Attendance model"""
    class Meta:
        model = Attendance
        fields = ['attendance_id', 'employee_id', 'attendance_date', 'status', 'notes', 'is_active', 'created_at', 'updated_at']
        read_only_fields = ['attendance_id', 'created_at', 'updated_at']


class AttendanceCreateSerializer(serializers.ModelSerializer):
    """Serializer for creating attendance records"""
    class Meta:
        model = Attendance
        fields = ['employee_id', 'attendance_date', 'status', 'notes']
    
    def validate(self, attrs):
        """Validate attendance record"""
        employee_id = attrs.get('employee_id')
        attendance_date = attrs.get('attendance_date')
        
        # Check if employee exists and is active
        try:
            employee = Employee.objects.get(id=employee_id, is_active=True)
        except Employee.DoesNotExist:
            raise serializers.ValidationError("Employee not found or inactive")
        
        # Check if attendance record already exists for this date
        if Attendance.objects.filter(
            employee_id=employee_id,
            attendance_date=attendance_date,
            is_active=True
        ).exists():
            raise serializers.ValidationError("Attendance record already exists for this date")
        
        # Check if employee is on leave for this date
        from leave.models import LeaveRecord
        if LeaveRecord.objects.filter(
            employee_id=employee_id,
            leave_date=attendance_date,
            is_active=True
        ).exists():
            raise serializers.ValidationError("Employee is on leave for this date")
        
        return attrs


class AttendanceUpdateSerializer(serializers.ModelSerializer):
    """Serializer for updating attendance records"""
    class Meta:
        model = Attendance
        fields = ['status', 'notes']


class AttendanceSummarySerializer(serializers.ModelSerializer):
    """Serializer for AttendanceSummary model"""
    class Meta:
        model = AttendanceSummary
        fields = ['date', 'total_employees', 'present_count', 'absent_count', 'leave_count', 'created_at', 'updated_at']
        read_only_fields = ['created_at', 'updated_at']


class PunchInSerializer(serializers.Serializer):
    """Serializer for punch in functionality"""
    employee_id = serializers.CharField(required=True)
    
    def validate_employee_id(self, value):
        """Validate employee ID"""
        try:
            employee = Employee.objects.get(id=value, is_active=True)
            return value
        except Employee.DoesNotExist:
            raise serializers.ValidationError("Employee not found or inactive")


class AttendanceReportSerializer(serializers.Serializer):
    """Serializer for attendance report generation"""
    employee_id = serializers.CharField(required=False)
    department = serializers.CharField(required=False)
    start_date = serializers.DateField(required=True)
    end_date = serializers.DateField(required=True)
    
    def validate(self, attrs):
        """Validate date range"""
        start_date = attrs.get('start_date')
        end_date = attrs.get('end_date')
        
        if start_date > end_date:
            raise serializers.ValidationError("Start date must be before end date")
        
        return attrs


class DailyAttendanceSerializer(serializers.Serializer):
    """Serializer for daily attendance view"""
    date = serializers.DateField(required=True)
    employee_id = serializers.CharField(required=False)
    
    def validate(self, attrs):
        """Validate date"""
        date = attrs.get('date')
        if date > timezone.now().date():
            raise serializers.ValidationError("Date cannot be in the future")
        return attrs
