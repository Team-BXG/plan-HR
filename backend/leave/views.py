from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from .models import LeaveBalance, LeaveRecord
from .serializers import LeaveBalanceSerializer, LeaveRecordSerializer
from .services import LeaveService

class LeaveViewSet(viewsets.ModelViewSet):
    queryset = LeaveRecord.objects.all()
    serializer_class = LeaveRecordSerializer

    @action(detail=False, methods=['post'])
    def apply(self, request):
        employee_id = request.data.get('employee_id')
        leave_date = request.data.get('leave_date')
        reason = request.data.get('reason')
        try:
            record = LeaveService.record_leave(employee_id, leave_date, reason)
            return Response(LeaveRecordSerializer(record).data, status=status.HTTP_201_CREATED)
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)

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
