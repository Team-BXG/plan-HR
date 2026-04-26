from .models import LeaveBalance, LeaveRecord
from employees.models import Employee
from datetime import date, datetime

class LeaveService:
    @staticmethod
    def get_leave_balance(employee_id_str, year=None):
        if not year:
            year = date.today().year
        employee = Employee.objects.get(id=employee_id_str)
        balance, created = LeaveBalance.objects.get_or_create(
            employee=employee, year=year,
            defaults={'yearly_allowance': 20, 'used_days': 0}
        )
        return balance

    @staticmethod
    def record_leave(employee_id_str, leave_date_str, reason):
        employee = Employee.objects.get(id=employee_id_str)
        
        # parse date
        if isinstance(leave_date_str, str):
            leave_date = datetime.strptime(leave_date_str, '%Y-%m-%d').date()
        else:
            leave_date = leave_date_str

        # Check balance
        balance = LeaveService.get_leave_balance(employee_id_str, leave_date.year)
        if balance.used_days >= balance.yearly_allowance:
            raise ValueError("Leave allowance exceeded for the year.")
        
        record = LeaveRecord.objects.create(
            employee=employee,
            leave_date=leave_date,
            reason=reason
        )
        balance.used_days += 1
        balance.save()
        return record
