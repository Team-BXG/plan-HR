from django.contrib import admin
from .models import Employee


@admin.register(Employee)
class EmployeeAdmin(admin.ModelAdmin):
    list_display = ['id', 'name', 'department', 'position', 'phone_number', 'is_active']
    list_filter = ['department', 'position', 'sex', 'is_active']
    search_fields = ['id', 'name', 'department', 'position']
    ordering = ['name']
    
    fieldsets = (
        ('Basic Information', {
            'fields': ('id', 'name', 'phone_number', 'sex', 'date_of_birth')
        }),
        ('Employment Information', {
            'fields': ('department', 'position', 'education', 'salary', 'join_date')
        }),
        ('Status', {
            'fields': ('is_active',)
        }),
    )
    
    def save_model(self, request, obj, form, change):
        """Override save to handle department count updates"""
        if not change:  # New employee
            from departments.services import DepartmentService
            dept_service = DepartmentService()
            dept_service.update_employee_count(obj.department, 1)
        super().save_model(request, obj, form, change)
