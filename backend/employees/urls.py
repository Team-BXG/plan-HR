from django.urls import path
from . import views

urlpatterns = [
    path('', views.employee_list_view, name='employee_list'),
    path('create/', views.employee_create_view, name='employee_create'),
    path('<str:employee_id>/', views.employee_detail_view, name='employee_detail'),
    path('<str:employee_id>/update/', views.employee_update_view, name='employee_update'),
    path('<str:employee_id>/delete/', views.employee_delete_view, name='employee_delete'),
    path('<str:employee_id>/master-detail/', views.employee_master_detail_view, name='employee_master_detail'),
    path('inactive/', views.inactive_employees_view, name='inactive_employees'),
    path('<str:employee_id>/reactivate/', views.reactivate_employee_view, name='reactivate_employee'),
    path('search/', views.employee_search_view, name='employee_search'),
    path('filter/', views.employee_filter_view, name='employee_filter'),
]
