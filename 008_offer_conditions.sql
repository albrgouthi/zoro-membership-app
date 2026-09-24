alter table public.offers add column if not exists min_spend_usd_cents integer;

-- Example: "Spend $200+, get 15% off" at The Corner Bistro
insert into public.offers (merchant_id, title_i18n, terms_i18n, discount_type, value, min_spend_usd_cents, status, starts_at, expires_at)
select id,
       '{"en":"15% off orders $200+","es":"15% de descuento en pedidos de $200+","ar":"خصم 15% على الطلبات فوق 200 دولار"}',
       '{"en":"Applies to total before tax. One redemption per visit.","es":"Aplica al total antes de impuestos. Un canje por visita.","ar":"ينطبق على الإجمالي قبل الضريبة. استخدام واحد لكل زيارة."}',
       'percent', 15, 20000, 'active', current_date, current_date + interval '90 days'
from public.merchants where legal_name = 'Demo Bistro LLC';
