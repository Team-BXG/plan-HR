from django.db import models


class Department(models.Model):
    """Department model matching Java schema"""
    department_id = models.AutoField(primary_key=True)
    department_name = models.CharField(max_length=100, unique=True)
    employee_count = models.IntegerField(default=0)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    
    class Meta:
        db_table = 'departments'
        verbose_name = 'Department'
        verbose_name_plural = 'Departments'
        ordering = ['department_name']
    
    def __str__(self):
        return self.department_name
    
    def increment_employee_count(self):
        """Increment employee count"""
        self.employee_count += 1
        self.save()
    
    def decrement_employee_count(self):
        """Decrement employee count"""
        if self.employee_count > 0:
            self.employee_count -= 1
            self.save()
