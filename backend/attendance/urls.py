from django.urls import path
from . import views

urlpatterns = [
    path('', views.attendance_list_view, name='attendance_list'),
    path('create/', views.attendance_create_view, name='attendance_create'),
    path('punch/', views.punch_in_view, name='punch_in'),
    path('daily/', views.daily_attendance_view, name='daily_attendance'),
    path('report/', views.attendance_report_view, name='attendance_report'),
    path('summary/', views.attendance_summary_view, name='attendance_summary'),
    path('today/', views.today_attendance_view, name='today_attendance'),
    path('<int:attendance_id>/', views.attendance_detail_view, name='attendance_detail'),
    path('<int:attendance_id>/update/', views.attendance_update_view, name='attendance_update'),
]
