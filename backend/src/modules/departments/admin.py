from django.contrib import admin
from .models import Department


@admin.register(Department)
class DepartmentAdmin(admin.ModelAdmin):
    list_display = ['department_id', 'department_name', 'employee_count', 'created_at', 'updated_at']
    list_filter = ['created_at', 'updated_at']
    search_fields = ['department_name']
    ordering = ['department_name']
    readonly_fields = ['employee_count', 'created_at', 'updated_at']
    
    def save_model(self, request, obj, form, change):
        """Override save to handle employee count updates"""
        if not change:  # New department
            obj.employee_count = 0
        super().save_model(request, obj, form, change)
