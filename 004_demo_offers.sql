insert into public.offers (merchant_id, title_i18n, terms_i18n, discount_type, value, status, starts_at, expires_at)
select id, '{"en":"20% off total bill","es":"20% de descuento en la cuenta","ar":"خصم 20% على الفاتورة"}',
       '{"en":"Valid for members only. Dine-in only.","es":"Solo para miembros. Solo para comer en el restaurante.","ar":"للأعضاء فقط. للتناول داخل المطعم فقط."}',
       'percent', 20, 'active', current_date, current_date + interval '90 days'
from public.merchants where legal_name = 'Demo Bistro LLC';

insert into public.offers (merchant_id, title_i18n, terms_i18n, discount_type, value, status, starts_at, expires_at)
select id, '{"en":"Free coffee with any entrée","es":"Café gratis con cualquier plato principal","ar":"قهوة مجانية مع أي طبق رئيسي"}',
       '{"en":"One per visit.","es":"Uno por visita.","ar":"واحدة لكل زيارة."}',
       'bogo', null, 'active', current_date, current_date + interval '90 days'
from public.merchants where legal_name = 'Sunrise Diner Inc';

insert into public.offers (merchant_id, title_i18n, terms_i18n, discount_type, value, status, starts_at, expires_at)
select id, '{"en":"$15 off first visit","es":"$15 de descuento en la primera visita","ar":"خصم 15 دولار على الزيارة الأولى"}',
       '{"en":"New patients only.","es":"Solo pacientes nuevos.","ar":"للمرضى الجدد فقط."}',
       'fixed', 15, 'active', current_date, current_date + interval '90 days'
from public.merchants where legal_name = 'CityCare Clinic LLC';
