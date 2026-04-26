from rest_framework import serializers
from .models import LeaveBalance, LeaveRecord

class LeaveBalanceSerializer(serializers.ModelSerializer):
    employee_name = serializers.CharField(source='employee.name', read_only=True)
    employee_id = serializers.CharField(source='employee.id', read_only=True)

    class Meta:
        model = LeaveBalance
        fields = ['id', 'employee', 'employee_name', 'employee_id', 'year', 'yearly_allowance', 'used_days']

class LeaveRecordSerializer(serializers.ModelSerializer):
    employee_name = serializers.CharField(source='employee.name', read_only=True)

    class Meta:
        model = LeaveRecord
        fields = ['id', 'employee', 'employee_name', 'leave_date', 'reason']
