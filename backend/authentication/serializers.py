from rest_framework import serializers
from django.contrib.auth import authenticate
from .models import User, Role


class UserSerializer(serializers.ModelSerializer):
    """Serializer for User model"""
    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'employee_id', 'role', 'phone_number', 
                 'department', 'position', 'education', 'sex', 'salary', 
                 'join_date', 'date_of_birth', 'is_active']
        read_only_fields = ['id']


class LoginSerializer(serializers.Serializer):
    """Serializer for login"""
    username = serializers.CharField()
    password = serializers.CharField()
    
    def validate(self, attrs):
        username = attrs.get('username')
        password = attrs.get('password')
        
        if username and password:
            user = authenticate(username=username, password=password)
            if not user:
                raise serializers.ValidationError('Invalid credentials')
            if not user.is_active:
                raise serializers.ValidationError('User account is disabled')
            attrs['user'] = user
            return attrs
        else:
            raise serializers.ValidationError('Must include username and password')


class ChangePasswordSerializer(serializers.Serializer):
    """Serializer for password change"""
    old_password = serializers.CharField(required=True)
    new_password = serializers.CharField(required=True, min_length=6, max_length=6)
    confirm_password = serializers.CharField(required=True)
    
    def validate(self, attrs):
        if attrs['new_password'] != attrs['confirm_password']:
            raise serializers.ValidationError("New passwords don't match")
        
        # Password must be exactly 6 alphanumeric characters
        if not attrs['new_password'].isalnum() or len(attrs['new_password']) != 6:
            raise serializers.ValidationError("Password must be exactly 6 letters and/or numbers")
        
        return attrs


class RoleSerializer(serializers.ModelSerializer):
    """Serializer for Role model"""
    class Meta:
        model = Role
        fields = ['id', 'user', 'role', 'is_active', 'created_at']
        read_only_fields = ['id', 'created_at']
