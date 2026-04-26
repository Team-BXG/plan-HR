from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from .models import User, Role


@admin.register(User)
class UserAdmin(BaseUserAdmin):
    list_display = ['username', 'email', 'employee_id', 'role', 'department', 'position', 'is_active']
    list_filter = ['role', 'department', 'is_active']
    search_fields = ['username', 'employee_id', 'email']
    ordering = ['username']
    
    fieldsets = (
        (None, {'fields': ('username', 'password')}),
        ('Personal info', {'fields': ('first_name', 'last_name', 'email', 'employee_id', 'phone_number', 'date_of_birth', 'sex')}),
        ('Employment info', {'fields': ('role', 'department', 'position', 'education', 'salary', 'join_date')}),
        ('Permissions', {'fields': ('is_active', 'is_staff', 'is_superuser', 'groups', 'user_permissions')}),
    )


@admin.register(Role)
class RoleAdmin(admin.ModelAdmin):
    list_display = ['user', 'role', 'is_active', 'created_at']
    list_filter = ['role', 'is_active', 'created_at']
    search_fields = ['user__username', 'role']
    ordering = ['-created_at']
