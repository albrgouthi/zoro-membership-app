-- Temporary: allow merchants without a signed-up owner yet, so we can
-- demo the store-list screen before auth/signup exists.
alter table public.merchants alter column owner_user_id drop not null;

-- Demo stores (2-3 per category) so the "tap a category" screen has content.
-- These will be replaced by real merchant signups once auth is built.
insert into public.merchants (category_id, legal_name, display_name_i18n, bio_i18n, status)
select id, 'Demo Bistro LLC', '{"en":"The Corner Bistro","es":"El Bistró de la Esquina","ar":"بيسترو الزاوية"}',
       '{"en":"American comfort food, family owned.","es":"Comida casera americana, negocio familiar.","ar":"طعام أمريكي منزلي، عائلي."}', 'approved'
from public.categories where name_i18n->>'en' = 'Dining';

insert into public.merchants (category_id, legal_name, display_name_i18n, bio_i18n, status)
select id, 'Sunrise Diner Inc', '{"en":"Sunrise Diner","es":"Restaurante Sunrise","ar":"مطعم صنرايز"}',
       '{"en":"Breakfast all day.","es":"Desayuno todo el día.","ar":"إفطار طوال اليوم."}', 'approved'
from public.categories where name_i18n->>'en' = 'Dining';

insert into public.merchants (category_id, legal_name, display_name_i18n, bio_i18n, status)
select id, 'CityCare Clinic LLC', '{"en":"CityCare Clinic","es":"Clínica CityCare","ar":"عيادة سيتي كير"}',
       '{"en":"Walk-in urgent care.","es":"Atención urgente sin cita.","ar":"رعاية طارئة بدون موعد."}', 'approved'
from public.categories where name_i18n->>'en' = 'Healthcare';

insert into public.merchants (category_id, legal_name, display_name_i18n, bio_i18n, status)
select id, 'Main St Retail LLC', '{"en":"Main Street Outfitters","es":"Main Street Outfitters","ar":"ماين ستريت أوتفيترز"}',
       '{"en":"Local clothing and gear.","es":"Ropa y equipo local.","ar":"ملابس ومعدات محلية."}', 'approved'
from public.categories where name_i18n->>'en' = 'Retail';

insert into public.merchants (category_id, legal_name, display_name_i18n, bio_i18n, status)
select id, 'Peak Fitness LLC', '{"en":"Peak Fitness Studio","es":"Estudio Peak Fitness","ar":"استوديو بيك فيتنس"}',
       '{"en":"Group classes and open gym.","es":"Clases grupales y gimnasio abierto.","ar":"حصص جماعية وصالة مفتوحة."}', 'approved'
from public.categories where name_i18n->>'en' = 'Fitness';
