from rest_framework import serializers
from .models import Employee
from src.modules.departments.models import Department


class EmployeeSerializer(serializers.ModelSerializer):
    """Serializer for Employee model"""
    employee_id = serializers.CharField(source='id', read_only=True)

    class Meta:
        model = Employee
        fields = ['id', 'employee_id', 'name', 'department', 'position', 'phone_number',
                 'education', 'sex', 'salary', 'join_date', 'date_of_birth',
                 'password', 'is_active']


class EmployeeCreateSerializer(serializers.ModelSerializer):
    """Serializer for creating employees"""
    password = serializers.CharField(write_only=True, min_length=6, max_length=6)
    
    class Meta:
        model = Employee
        fields = ['id', 'name', 'department', 'position', 'phone_number',
                 'education', 'sex', 'salary', 'join_date', 'date_of_birth', 'password']
    
    def validate_id(self, value):
        """Validate employee ID is 4-5 alphanumeric characters"""
        if not value:
            raise serializers.ValidationError("Employee ID is required")
        return value
    
    def validate_name(self, value):
        """Validate name is 10-20 characters with space"""
        if len(value) < 10 or len(value) > 20 or ' ' not in value:
            raise serializers.ValidationError("Name must be 10-20 characters with space")
        return value
    
    def validate_phone_number(self, value):
        """Validate phone number starts with 09 and is 10 digits"""
        if not value.startswith('09') or len(value) != 10 or not value.isdigit():
            raise serializers.ValidationError("Phone must be 10 digits starting with 09")
        return value
    
    def validate(self, attrs):
        """Cross-field validation"""
        # Check if department exists
        if not Department.objects.filter(department_name=attrs['department']).exists():
            raise serializers.ValidationError("Department does not exist")
        
        return attrs


class EmployeeUpdateSerializer(serializers.ModelSerializer):
    """Serializer for updating employees"""
    class Meta:
        model = Employee
        fields = ['name', 'department', 'position', 'phone_number', 
                 'education', 'sex', 'salary']
    
    def validate_name(self, value):
        """Validate name is 10-20 characters with space"""
        if len(value) < 10 or len(value) > 20 or ' ' not in value:
            raise serializers.ValidationError("Name must be 10-20 characters with space")
        return value
    
    def validate_phone_number(self, value):
        """Validate phone number starts with 09 and is 10 digits"""
        if not value.startswith('09') or len(value) != 10 or not value.isdigit():
            raise serializers.ValidationError("Phone must be 10 digits starting with 09")
        return value


class InactiveEmployeeSerializer(serializers.ModelSerializer):
    """Compatibility serializer for inactive employees endpoint"""
    employee_id = serializers.CharField(source='id', read_only=True)

    class Meta:
        model = Employee
        fields = ['id', 'employee_id', 'name', 'department', 'position', 'phone_number',
                 'education', 'sex', 'salary', 'join_date', 'date_of_birth',
                 'is_active']


class EmployeeSearchSerializer(serializers.Serializer):
    """Serializer for employee search parameters"""
    employee_id = serializers.CharField(required=False)
    name = serializers.CharField(required=False)
    department = serializers.CharField(required=False)
    position = serializers.CharField(required=False)
    sex = serializers.CharField(required=False)
    education = serializers.CharField(required=False)
    salary_min = serializers.DecimalField(required=False, max_digits=10, decimal_places=2)
    salary_max = serializers.DecimalField(required=False, max_digits=10, decimal_places=2)
    join_date_from = serializers.DateField(required=False)
    join_date_to = serializers.DateField(required=False)
    seniority = serializers.CharField(required=False)  # >1 Year, >3 Years, etc.
    
    def validate_seniority(self, value):
        """Validate seniority options"""
        valid_options = ['>1 Year', '>3 Years', '>5 Years', '>10 Years']
        if value and value not in valid_options:
            raise serializers.ValidationError("Invalid seniority option")
        return value


class EmployeeFilterSerializer(serializers.Serializer):
    """Serializer for advanced employee filtering"""
    department = serializers.CharField(required=False)
    position = serializers.CharField(required=False)
    gender = serializers.CharField(required=False)
    seniority = serializers.CharField(required=False)
    salary_range = serializers.CharField(required=False)  # <10,000, 10,000-30,000, etc.
    
    def validate_seniority(self, value):
        """Validate seniority options"""
        valid_options = ['All', '>1 Year', '>3 Years', '>5 Years']
        if value and value not in valid_options:
            raise serializers.ValidationError("Invalid seniority option")
        return value
    
    def validate_salary_range(self, value):
        """Validate salary range options"""
        valid_options = ['All', '<10,000', '10,000-30,000', '30,000-50,000', '>50,000']
        if value and value not in valid_options:
            raise serializers.ValidationError("Invalid salary range option")
        return value
