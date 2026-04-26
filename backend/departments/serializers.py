from rest_framework import serializers
from .models import Department


class DepartmentSerializer(serializers.ModelSerializer):
    """Serializer for Department model"""
    class Meta:
        model = Department
        fields = ['department_id', 'department_name', 'employee_count', 'created_at', 'updated_at']
        read_only_fields = ['department_id', 'employee_count', 'created_at', 'updated_at']


class DepartmentCreateSerializer(serializers.ModelSerializer):
    """Serializer for creating departments"""
    class Meta:
        model = Department
        fields = ['department_name']
    
    def validate_department_name(self, value):
        """Validate department name is unique"""
        if Department.objects.filter(department_name__iexact=value).exists():
            raise serializers.ValidationError("Department with this name already exists")
        return value
