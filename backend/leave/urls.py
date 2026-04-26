from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import LeaveViewSet, LeaveBalanceViewSet

router = DefaultRouter()
router.register(r'records', LeaveViewSet, basename='leave-records')
router.register(r'balances', LeaveBalanceViewSet, basename='leave-balances')

urlpatterns = [
    path('', include(router.urls)),
]
