from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.pagination import PageNumberPagination
from django.shortcuts import get_object_or_404
from .models import Department
from .serializers import DepartmentSerializer, DepartmentCreateSerializer
from .services import DepartmentService


class DepartmentPagination(PageNumberPagination):
    """Custom pagination for departments"""
    page_size = 10
    page_size_query_param = 'page_size'
    max_page_size = 100


@api_view(['GET'])
def department_list_view(request):
    """Get list of all departments"""
    department_service = DepartmentService()
    
    # Handle search
    search = request.GET.get('search', '')
    if search:
        departments = department_service.search_departments(search)
    else:
        departments = department_service.get_all_departments()
    
    if not request.GET.get('page'):
        serializer = DepartmentSerializer(departments, many=True)
        return Response(serializer.data)

    # Paginate results
    paginator = DepartmentPagination()
    paginated_departments = paginator.paginate_queryset(departments, request)
    serializer = DepartmentSerializer(paginated_departments, many=True)
    
    return paginator.get_paginated_response(serializer.data)


@api_view(['POST'])
def department_create_view(request):
    """Create new department"""
    serializer = DepartmentCreateSerializer(data=request.data)
    if serializer.is_valid():
        department_service = DepartmentService()
        result = department_service.create_department(
            serializer.validated_data['department_name']
        )
        
        if result['success']:
            return Response({
                'message': 'Department created successfully',
                'department': result['department']
            }, status=status.HTTP_201_CREATED)
        else:
            return Response({'error': result['message']}, 
                        status=status.HTTP_400_BAD_REQUEST)
    
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@api_view(['GET'])
def department_detail_view(request, department_id):
    """Get department details"""
    department_service = DepartmentService()
    department = department_service.get_department_by_id(department_id)
    
    if not department:
        return Response({'error': 'Department not found'}, 
                    status=status.HTTP_404_NOT_FOUND)
    
    serializer = DepartmentSerializer(department)
    return Response(serializer.data)


@api_view(['PUT'])
def department_update_view(request, department_id):
    """Update department"""
    department_service = DepartmentService()
    
    # Check if department exists
    department = department_service.get_department_by_id(department_id)
    if not department:
        return Response({'error': 'Department not found'}, 
                    status=status.HTTP_404_NOT_FOUND)
    
    # Update department
    new_name = request.data.get('department_name')
    if not new_name:
        return Response({'error': 'Department name is required'}, 
                    status=status.HTTP_400_BAD_REQUEST)
    
    result = department_service.update_department(department_id, new_name)
    
    if result['success']:
        return Response({
            'message': 'Department updated successfully',
            'department': result['department']
        })
    else:
        return Response({'error': result['message']}, 
                    status=status.HTTP_400_BAD_REQUEST)


@api_view(['DELETE'])
def department_delete_view(request, department_id):
    """Delete department"""
    department_service = DepartmentService()
    
    result = department_service.delete_department(department_id)
    
    if result['success']:
        return Response({'message': 'Department deleted successfully'})
    else:
        return Response({'error': result['message']}, 
                    status=status.HTTP_400_BAD_REQUEST)
