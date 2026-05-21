from django.db import models
from src.modules.employees.models import Employee

class LeaveBalance(models.Model):
    employee = models.ForeignKey(Employee, on_delete=models.CASCADE, related_name='leave_balances')
    year = models.IntegerField()
    yearly_allowance = models.IntegerField(default=20)
    used_days = models.IntegerField(default=0)
    
    class Meta:
        db_table = 'leave_balance'

class LeaveRecord(models.Model):
    employee = models.ForeignKey(Employee, on_delete=models.CASCADE, related_name='leave_records')
    leave_date = models.DateField()
    reason = models.TextField()
    status = models.CharField(max_length=20, default='pending', choices=[('pending', 'Pending'), ('approved', 'Approved'), ('rejected', 'Rejected')])
    
    class Meta:
        db_table = 'leave_records'
