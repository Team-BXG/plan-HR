from datetime import datetime, timedelta
from django.utils import timezone
from src.modules.leave.models import LeaveRecord
from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.pagination import PageNumberPagination
from rest_framework.response import Response
from .models import Attendance


class AttendancePagination(PageNumberPagination):
    page_size = 10


@api_view(['GET'])
def attendance_list_view(request):
    department = request.GET.get('department')
    queryset = Attendance.objects.all().order_by('-attendance_date')
    
    if department:
        queryset = queryset.filter(employee__department=department)
        
    paginator = AttendancePagination()
    page = paginator.paginate_queryset(queryset, request)
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
    
    # Check if on leave
    is_on_leave = LeaveRecord.objects.filter(employee_id=employee_id, leave_date=today).exists()
    if is_on_leave:
        return Response({'message': 'You are on leave', 'status': 'leave'}, status=status.HTTP_400_BAD_REQUEST)

    existing = Attendance.objects.filter(employee_id=employee_id, attendance_date=today).first()
    if existing:
        return Response(
            {'message': 'Already punched in today', 'status': 'already_punched'},
            status=status.HTTP_400_BAD_REQUEST
        )
    row = Attendance.objects.create(employee_id=employee_id, attendance_date=today)
    return Response(
        {'message': 'Punched in successfully', 'status': 'success', 'attendance': {'attendance_id': row.attendance_id, 'employee_id': employee_id, 'attendance_date': today}},
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
    employee_id = request.data.get('employee_id')
    from_date_str = request.data.get('from_date')
    to_date_str = request.data.get('to_date')
    
    if not employee_id or not from_date_str or not to_date_str:
        return Response({'error': 'employee_id, from_date, and to_date are required'}, status=status.HTTP_400_BAD_REQUEST)
        
    try:
        from_date = datetime.strptime(from_date_str, '%Y-%m-%d').date()
        to_date = datetime.strptime(to_date_str, '%Y-%m-%d').date()
    except ValueError:
        return Response({'error': 'Invalid date format. Use YYYY-MM-DD.'}, status=status.HTTP_400_BAD_REQUEST)
        
    if from_date > to_date:
        return Response({'error': 'from_date cannot be after to_date'}, status=status.HTTP_400_BAD_REQUEST)
        
    attendances = Attendance.objects.filter(employee_id=employee_id, attendance_date__range=[from_date, to_date])
    present_dates = {a.attendance_date for a in attendances}
    
    leaves = LeaveRecord.objects.filter(employee_id=employee_id, leave_date__range=[from_date, to_date])
    leave_dates = {l.leave_date for l in leaves}
    
    report_data = []
    current_date = from_date
    while current_date <= to_date:
        if current_date in present_dates:
            status_str = 'Present'
        elif current_date in leave_dates:
            status_str = 'Leave'
        else:
            status_str = 'Absent'
            
        report_data.append({
            'date': current_date.strftime('%Y-%m-%d'),
            'status': status_str
        })
        current_date += timedelta(days=1)
        
    return Response({'report_data': report_data})

@api_view(['GET'])
def employee_stats_view(request):
    employee_id = request.GET.get('employee_id')
    if not employee_id:
        return Response({'error': 'employee_id required'}, status=status.HTTP_400_BAD_REQUEST)
        
    # Get all time stats for simplicity
    present = Attendance.objects.filter(employee_id=employee_id).count()
    leave = LeaveRecord.objects.filter(employee_id=employee_id).count()
    
    # Calculate absent days by assuming the employee should be present every weekday since join date? 
    # That might be too complex for now, we can just return 0 or calculate from the start of the year.
    # Let's just return present and leave, and absent as 0 for now to keep it simple, or calculate it.
    
    # To calculate absent, let's just find the first punch in, and count days till today.
    first_punch = Attendance.objects.filter(employee_id=employee_id).order_by('attendance_date').first()
    absent = 0
    if first_punch:
        total_days = (timezone.now().date() - first_punch.attendance_date).days + 1
        absent = max(0, total_days - present - leave)
        
    return Response({
        'present': present,
        'absent': absent,
        'leave': leave
    })


@api_view(['GET'])
def attendance_summary_view(request):
    return Response({'present': Attendance.objects.count(), 'absent': 0, 'leave': 0, 'total': Attendance.objects.count()})


@api_view(['GET'])
def today_attendance_view(request):
    today = timezone.now().date()
    count = Attendance.objects.filter(attendance_date=today).count()
    return Response({'present': count, 'absent': 0, 'leave': 0, 'total': count})
