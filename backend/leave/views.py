from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from .models import LeaveBalance, LeaveRecord
from .serializers import LeaveBalanceSerializer, LeaveRecordSerializer
from .services import LeaveService

class LeaveViewSet(viewsets.ModelViewSet):
    queryset = LeaveRecord.objects.all()
    serializer_class = LeaveRecordSerializer

    def list(self, request, *args, **kwargs):
        employee_id = request.query_params.get('employee')
        department = request.query_params.get('department')
        start_date = request.query_params.get('start_date')
        end_date = request.query_params.get('end_date')
        
        queryset = self.get_queryset()
        if employee_id:
            queryset = queryset.filter(employee_id=employee_id)
        if department:
            queryset = queryset.filter(employee__department=department)
        if start_date and end_date:
            queryset = queryset.filter(leave_date__range=[start_date, end_date])
            
        serializer = self.get_serializer(queryset, many=True)
        # map to start_date/end_date for UI
        data = []
        for item in serializer.data:
            data.append({
                'id': item['id'],
                'employee': item['employee'],
                'employee_name': item['employee_name'],
                'start_date': item['leave_date'],
                'end_date': item['leave_date'],
                'reason': item['reason'],
                'status': item.get('status', 'pending')
            })
        return Response(data)

    @action(detail=True, methods=['patch'])
    def update_status(self, request, pk=None):
        leave_record = self.get_object()
        status_val = request.data.get('status')
        if status_val in ['pending', 'approved', 'rejected']:
            leave_record.status = status_val
            leave_record.save(update_fields=['status'])
            return Response({'message': f'Leave status updated to {status_val}'})
        return Response({'error': 'Invalid status'}, status=status.HTTP_400_BAD_REQUEST)

    @action(detail=False, methods=['post'])
    def apply(self, request):
        employee_id = request.data.get('employee') or request.data.get('employee_id')
        start_date = request.data.get('start_date')
        end_date = request.data.get('end_date')
        reason = request.data.get('reason')
        try:
            records = LeaveService.record_leave(employee_id, start_date, end_date, reason)
            return Response(LeaveRecordSerializer(records, many=True).data, status=status.HTTP_201_CREATED)
        except Exception as e:
            return Response({"error": str(e), "message": str(e)}, status=status.HTTP_400_BAD_REQUEST)

class LeaveBalanceViewSet(viewsets.ModelViewSet):
    queryset = LeaveBalance.objects.all()
    serializer_class = LeaveBalanceSerializer

    @action(detail=False, methods=['get'])
    def my_balance(self, request):
        employee_id = request.query_params.get('employee_id')
        try:
            balance = LeaveService.get_leave_balance(employee_id)
            return Response(LeaveBalanceSerializer(balance).data)
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)
