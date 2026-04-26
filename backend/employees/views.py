from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.pagination import PageNumberPagination
from django.db.models import Q
from .models import Employee
from .serializers import (
    EmployeeSerializer, EmployeeCreateSerializer, EmployeeUpdateSerializer,
    InactiveEmployeeSerializer, EmployeeSearchSerializer, EmployeeFilterSerializer
)


class EmployeePagination(PageNumberPagination):
    """Custom pagination for employees"""
    page_size = 10
    page_size_query_param = 'page_size'
    max_page_size = 100


@api_view(['GET'])
def employee_list_view(request):
    """Get list of employees"""
    search = request.GET.get('search', '')
    employees = Employee.objects.all().order_by('name')
    if search:
        employees = employees.filter(Q(id__icontains=search) | Q(name__icontains=search))
    
    # Paginate results
    paginator = EmployeePagination()
    paginated_employees = paginator.paginate_queryset(employees, request)
    serializer = EmployeeSerializer(paginated_employees, many=True)
    
    return paginator.get_paginated_response(serializer.data)


@api_view(['POST'])
def employee_create_view(request):
    """Create new employee"""
    serializer = EmployeeCreateSerializer(data=request.data)
    if serializer.is_valid():
        employee = serializer.save()
        return Response(
            {
                'message': 'Employee created successfully',
                'employee': EmployeeSerializer(employee).data
            },
            status=status.HTTP_201_CREATED
        )
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@api_view(['GET'])
def employee_detail_view(request, employee_id):
    """Get employee details"""
    try:
        employee = Employee.objects.get(id=employee_id)
    except Employee.DoesNotExist:
        return Response({'error': 'Employee not found'}, 
                    status=status.HTTP_404_NOT_FOUND)
    
    serializer = EmployeeSerializer(employee)
    return Response(serializer.data)


@api_view(['PUT'])
def employee_update_view(request, employee_id):
    """Update employee"""
    try:
        employee = Employee.objects.get(id=employee_id)
    except Employee.DoesNotExist:
        return Response({'error': 'Employee not found'}, 
                    status=status.HTTP_404_NOT_FOUND)
    
    serializer = EmployeeUpdateSerializer(employee, data=request.data, partial=True)
    if serializer.is_valid():
        updated = serializer.save()
        return Response({
            'message': 'Employee updated successfully',
            'employee': EmployeeSerializer(updated).data
        })
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@api_view(['DELETE'])
def employee_delete_view(request, employee_id):
    """Delete employee"""
    try:
        employee = Employee.objects.get(id=employee_id)
    except Employee.DoesNotExist:
        return Response({'error': 'Employee not found'}, status=status.HTTP_404_NOT_FOUND)
    employee.delete()
    return Response({'message': 'Employee deleted successfully'})


@api_view(['GET'])
def inactive_employees_view(request):
    """Get list of inactive employees"""
    inactive_employees = Employee.objects.filter(is_active=False).order_by('name')
    
    # Paginate results
    paginator = EmployeePagination()
    paginated_employees = paginator.paginate_queryset(inactive_employees, request)
    serializer = InactiveEmployeeSerializer(paginated_employees, many=True)
    
    return paginator.get_paginated_response(serializer.data)


@api_view(['POST'])
def reactivate_employee_view(request, employee_id):
    """Reactivate employee"""
    try:
        employee = Employee.objects.get(id=employee_id)
    except Employee.DoesNotExist:
        return Response({'error': 'Employee not found'}, status=status.HTTP_404_NOT_FOUND)
    employee.is_active = True
    employee.save(update_fields=['is_active'])
    return Response({'message': 'Employee reactivated successfully'})


@api_view(['POST'])
def employee_search_view(request):
    """Advanced employee search"""
    serializer = EmployeeSearchSerializer(data=request.data)
    if serializer.is_valid():
        employees = Employee.objects.all()
        data = serializer.validated_data
        if data.get('employee_id'):
            employees = employees.filter(id__icontains=data['employee_id'])
        if data.get('name'):
            employees = employees.filter(name__icontains=data['name'])
        if data.get('department'):
            employees = employees.filter(department=data['department'])
        
        # Paginate results
        paginator = EmployeePagination()
        paginated_employees = paginator.paginate_queryset(employees, request)
        employee_serializer = EmployeeSerializer(paginated_employees, many=True)
        
        return paginator.get_paginated_response(employee_serializer.data)
    
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@api_view(['POST'])
def employee_filter_view(request):
    """Advanced employee filtering"""
    serializer = EmployeeFilterSerializer(data=request.data)
    if serializer.is_valid():
        employees = Employee.objects.all()
        data = serializer.validated_data
        if data.get('department') and data['department'] != 'All':
            employees = employees.filter(department=data['department'])
        if data.get('position') and data['position'] != 'All':
            employees = employees.filter(position=data['position'])
        if data.get('gender') and data['gender'] != 'All':
            employees = employees.filter(sex=data['gender'])
        
        # Paginate results
        paginator = EmployeePagination()
        paginated_employees = paginator.paginate_queryset(employees, request)
        employee_serializer = EmployeeSerializer(paginated_employees, many=True)
        
        return paginator.get_paginated_response(employee_serializer.data)
    
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@api_view(['GET'])
def employee_master_detail_view(request, employee_id):
    """Get master detail view for employee"""
    try:
        employee = Employee.objects.get(id=employee_id)
    except Employee.DoesNotExist:
        return Response({'error': 'Employee not found'}, 
                    status=status.HTTP_404_NOT_FOUND)

    return Response(EmployeeSerializer(employee).data)
