from django.db import models
class Attendance(models.Model):
    attendance_id = models.AutoField(primary_key=True, db_column='attendance_id')
    employee_id = models.CharField(max_length=20)
    attendance_date = models.DateField()

    class Meta:
        db_table = 'attendance'
        unique_together = ['employee_id', 'attendance_date']
        ordering = ['-attendance_date']

    def __str__(self):
        return f"{self.employee_id} - {self.attendance_date}"


class AttendanceSummary(models.Model):
    """Model for daily attendance summary"""
    date = models.DateField(unique=True)
    total_employees = models.IntegerField(default=0)
    present_count = models.IntegerField(default=0)
    absent_count = models.IntegerField(default=0)
    leave_count = models.IntegerField(default=0)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    
    class Meta:
        db_table = 'attendance_summary'
        verbose_name = 'Attendance Summary'
        verbose_name_plural = 'Attendance Summaries'
        ordering = ['-date']
    
    def __str__(self):
        return f"Summary for {self.date}"
