from django.db import models
from employees.models import Employee

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
    
    class Meta:
        db_table = 'leave_records'
