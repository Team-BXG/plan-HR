from django.contrib import admin
from .models import LeaveBalance, LeaveRecord


@admin.register(LeaveRecord)
class LeaveRecordAdmin(admin.ModelAdmin):
    list_display = ['employee', 'leave_date', 'reason']
    list_filter = ['leave_date']
    search_fields = ['employee__id', 'employee__name', 'reason']
    ordering = ['-leave_date']
    
    fieldsets = (
        ('Leave Information', {
            'fields': ('employee', 'leave_date', 'reason')
        }),
    )


@admin.register(LeaveBalance)
class LeaveBalanceAdmin(admin.ModelAdmin):
    list_display = ['employee', 'year', 'yearly_allowance', 'used_days']
    list_filter = ['year']
    search_fields = ['employee__id', 'employee__name']
    ordering = ['-year', 'employee']
