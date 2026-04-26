from django.db import models

class Employee(models.Model):
    id = models.CharField(max_length=50, primary_key=True)
    name = models.CharField(max_length=100)
    education = models.CharField(max_length=100, blank=True, null=True)
    department = models.CharField(max_length=100, blank=True, null=True)
    sex = models.CharField(max_length=10, blank=True, null=True)
    date_of_birth = models.DateField(blank=True, null=True)
    join_date = models.DateField(blank=True, null=True)
    salary = models.DecimalField(max_digits=10, decimal_places=2, blank=True, null=True)
    position = models.CharField(max_length=100, blank=True, null=True)
    phone_number = models.CharField(max_length=15, blank=True, null=True)
    password = models.CharField(max_length=100)
    is_active = models.BooleanField(default=True, db_column='is_active')

    class Meta:
        managed = False
        db_table = 'employees'

class Role(models.Model):
    employee_id = models.CharField(max_length=50, primary_key=True)
    role = models.CharField(max_length=50)

    class Meta:
        managed = False
        db_table = 'roles'
