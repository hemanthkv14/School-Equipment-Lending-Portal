-- 1. Create categories (No FK dependencies)
CREATE TABLE public.categories (
  category_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  name character varying NOT NULL UNIQUE,
  CONSTRAINT categories_pkey PRIMARY KEY (category_id)
);

-- 2. Create users (No FK dependencies)
CREATE TABLE public.users (
  user_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  username character varying NOT NULL UNIQUE,
  email character varying NOT NULL UNIQUE,
  password_hash character varying NOT NULL,
  role character varying NOT NULL CHECK (upper(role::text) = ANY (ARRAY['STUDENT'::text, 'STAFF'::text, 'ADMIN'::text])),
  token character varying,
  created_at timestamp without time zone,
  name character varying,
  password character varying,
  CONSTRAINT users_pkey PRIMARY KEY (user_id)
);

-- 3. Create equipment (Depends on categories)
CREATE TABLE public.equipment (
  equipment_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  name character varying NOT NULL,
  category_id bigint NOT NULL,
  total_quantity integer NOT NULL CHECK (total_quantity >= 0),
  quantity_available integer NOT NULL CHECK (quantity_available >= 0),
  condition character varying,
  created_at timestamp without time zone,
  CONSTRAINT equipment_pkey PRIMARY KEY (equipment_id),
  CONSTRAINT equipment_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.categories(category_id)
);

-- 4. Create items (Depends on equipment)
CREATE TABLE public.items (
  item_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  equipment_id bigint NOT NULL,
  serial_number character varying UNIQUE,
  condition character varying NOT NULL CHECK (upper(condition::text) = ANY (ARRAY['NEW'::text, 'GOOD'::text, 'FAIR'::text, 'POOR'::text, 'BROKEN'::text])),
  is_available boolean NOT NULL DEFAULT true,
  CONSTRAINT items_pkey PRIMARY KEY (item_id),
  CONSTRAINT items_equipment_id_fkey FOREIGN KEY (equipment_id) REFERENCES public.equipment(equipment_id)
);

-- 5. Create lendings (Depends on items and users)
CREATE TABLE public.lendings (
  lending_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  item_id bigint NOT NULL UNIQUE,
  borrower_id bigint NOT NULL,
  request_date timestamp without time zone NOT NULL DEFAULT now(),
  approval_status character varying NOT NULL,
  authorized_by bigint,
  issue_date timestamp without time zone,
  CONSTRAINT lendings_pkey PRIMARY KEY (lending_id),
  CONSTRAINT lendings_authorized_by_fkey FOREIGN KEY (authorized_by) REFERENCES public.users(user_id),
  CONSTRAINT lendings_borrower_id_fkey FOREIGN KEY (borrower_id) REFERENCES public.users(user_id),
  CONSTRAINT lendings_item_id_fkey FOREIGN KEY (item_id) REFERENCES public.items(item_id)
);

-- 6. Create duetracking (Depends on lendings)
CREATE TABLE public.duetracking (
  due_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  lending_id bigint NOT NULL UNIQUE,
  due_date timestamp without time zone,
  return_date timestamp without time zone,
  is_overdue boolean,
  rejection_date timestamp without time zone,
  CONSTRAINT duetracking_pkey PRIMARY KEY (due_id),
  CONSTRAINT duetracking_lending_id_fkey FOREIGN KEY (lending_id) REFERENCES public.lendings(lending_id)
);

-- 7. Create notifications (Depends on users)
CREATE TABLE public.notifications (
  notification_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  recipient_id bigint NOT NULL,
  lending_id bigint,
  type character varying NOT NULL,
  message text NOT NULL,
  created_at timestamp without time zone NOT NULL DEFAULT now(),
  notification_sent boolean DEFAULT false,
  sent_at timestamp without time zone,
  CONSTRAINT notifications_pkey PRIMARY KEY (notification_id),
  CONSTRAINT notifications_recipient_id_fkey FOREIGN KEY (recipient_id) REFERENCES public.users(user_id)
);