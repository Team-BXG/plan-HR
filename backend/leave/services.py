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
    def record_leave(employee_id_str, start_date_str, end_date_str, reason):
        from datetime import timedelta
        employee = Employee.objects.get(id=employee_id_str)
        
        # parse dates
        start_date = datetime.strptime(start_date_str, '%Y-%m-%d').date() if isinstance(start_date_str, str) else start_date_str
        end_date = datetime.strptime(end_date_str, '%Y-%m-%d').date() if isinstance(end_date_str, str) else end_date_str

        if start_date > end_date:
            raise ValueError("Start date cannot be after end date.")
            
        current_date = start_date
        dates_to_book = []
        while current_date <= end_date:
            dates_to_book.append(current_date)
            current_date += timedelta(days=1)
            
        # Check if already booked
        existing = LeaveRecord.objects.filter(employee=employee, leave_date__in=dates_to_book)
        if existing.exists():
            already_booked = [r.leave_date.strftime('%Y-%m-%d') for r in existing]
            raise ValueError(f"Already on leave for dates: {', '.join(already_booked)}")
            
        # Check balance
        days_requested = len(dates_to_book)
        balance = LeaveService.get_leave_balance(employee_id_str, start_date.year)
        if balance.used_days + days_requested > balance.yearly_allowance:
            raise ValueError("Leave allowance exceeded for the year.")
            
        # Create records
        records = []
        for d in dates_to_book:
            record = LeaveRecord.objects.create(
                employee=employee,
                leave_date=d,
                reason=reason
            )
            records.append(record)
            
        balance.used_days += days_requested
        balance.save()
        
        return records
