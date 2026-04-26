from datetime import datetime
from django.utils import timezone
from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.pagination import PageNumberPagination
from rest_framework.response import Response
from .models import Attendance


class AttendancePagination(PageNumberPagination):
    page_size = 10


@api_view(['GET'])
def attendance_list_view(request):
    rows = Attendance.objects.all().order_by('-attendance_date')
    paginator = AttendancePagination()
    page = paginator.paginate_queryset(rows, request)
    data = [
        {
            "attendance_id": row.attendance_id,
            "employee_id": row.employee_id,
            "attendance_date": row.attendance_date,
        }
        for row in page
    ]
    return paginator.get_paginated_response(data)


@api_view(['POST'])
def attendance_create_view(request):
    employee_id = (request.data.get('employee_id') or '').strip()
    attendance_date = request.data.get('attendance_date') or timezone.now().date()
    if isinstance(attendance_date, str):
        attendance_date = datetime.fromisoformat(attendance_date).date()
    if not employee_id:
        return Response({'error': 'employee_id is required'}, status=status.HTTP_400_BAD_REQUEST)

    row = Attendance.objects.create(employee_id=employee_id, attendance_date=attendance_date)
    return Response(
        {
            'message': 'Attendance recorded successfully',
            'attendance': {
                'attendance_id': row.attendance_id,
                'employee_id': row.employee_id,
                'attendance_date': row.attendance_date,
            }
        },
        status=status.HTTP_201_CREATED
    )


@api_view(['GET'])
def attendance_detail_view(request, attendance_id):
    try:
        row = Attendance.objects.get(attendance_id=attendance_id)
    except Attendance.DoesNotExist:
        return Response({'error': 'Attendance record not found'}, status=status.HTTP_404_NOT_FOUND)
    return Response({
        'attendance_id': row.attendance_id,
        'employee_id': row.employee_id,
        'attendance_date': row.attendance_date,
    })


@api_view(['PUT'])
def attendance_update_view(request, attendance_id):
    return Response({'message': 'Not implemented in this rebuild'}, status=status.HTTP_200_OK)


@api_view(['POST'])
def punch_in_view(request):
    employee_id = (request.data.get('employee_id') or '').strip()
    if not employee_id:
        return Response({'error': 'employee_id is required'}, status=status.HTTP_400_BAD_REQUEST)
    today = timezone.now().date()
    existing = Attendance.objects.filter(employee_id=employee_id, attendance_date=today).first()
    if existing:
        return Response(
            {'message': 'Already punched in today', 'attendance': {'attendance_id': existing.attendance_id, 'employee_id': employee_id, 'attendance_date': today}},
            status=status.HTTP_200_OK
        )
    row = Attendance.objects.create(employee_id=employee_id, attendance_date=today)
    return Response(
        {'message': 'Punched in successfully', 'attendance': {'attendance_id': row.attendance_id, 'employee_id': employee_id, 'attendance_date': today}},
        status=status.HTTP_200_OK
    )


@api_view(['GET'])
def daily_attendance_view(request):
    employee_id = (request.GET.get('employee_id') or '').strip()
    date_str = request.GET.get('date')
    target_date = datetime.fromisoformat(date_str).date() if date_str else timezone.now().date()
    if not employee_id:
        return Response({'error': 'Employee ID is required'}, status=status.HTTP_400_BAD_REQUEST)
    row = Attendance.objects.filter(employee_id=employee_id, attendance_date=target_date).first()
    if not row:
        return Response({'employee_id': employee_id, 'date': target_date, 'status': 'Not Recorded', 'can_punch_in': True})
    return Response({'attendance': {'attendance_id': row.attendance_id, 'employee_id': employee_id, 'attendance_date': target_date}, 'can_punch_in': False})


@api_view(['POST'])
def attendance_report_view(request):
    return Response({'report_data': [], 'summary': {'total_records': 0}})


@api_view(['GET'])
def attendance_summary_view(request):
    return Response({'present': Attendance.objects.count(), 'absent': 0, 'leave': 0, 'total': Attendance.objects.count()})


@api_view(['GET'])
def today_attendance_view(request):
    today = timezone.now().date()
    count = Attendance.objects.filter(attendance_date=today).count()
    return Response({'present': count, 'absent': 0, 'leave': 0, 'total': count})
