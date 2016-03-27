--SQL PRINCIPAL PROYECTO

-- Table: "mp_tipo_producto"

-- DROP TABLE "mp_tipo_producto";

CREATE TABLE "mp_tipo_producto"
(
  id serial NOT NULL,
  tipo character varying(50) NOT NULL,
  descripcion character varying(255),
  CONSTRAINT "mp_tipo_producto_pkey" PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "mp_tipo_producto"
  OWNER TO postgres;

-- Table: "mp_direccion"

-- DROP TABLE "mp_direccion";

CREATE TABLE "mp_direccion"
(
  id serial NOT NULL,
  nombre character varying(100) NOT NULL,
  latitud double precision ,
  longitud double precision,
  ciudad character varying(50) NOT NULL,
  departamento character varying(50) NOT NULL,
  pais character varying(50) NOT NULL,
  CONSTRAINT "mp_direccion_pkey" PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "mp_direccion"
  OWNER TO postgres;
  
  
  -- Table: "mp_persona"

-- DROP TABLE "mp_persona";

CREATE TABLE "mp_persona"
(
  id serial NOT NULL,
  nombre character varying(50) NOT NULL,
  nombre_sec character varying(50) ,
  apellido character varying(50),
  apellido_sec character varying(50),
  telefono integer,
  correo_electronico character varying(50) NOT NULL,
  tipo character varying(15) NOT NULL,
  foto character varying(1000) ,
  fecha_registro timestamp with time zone NOT NULL,
  fecha_actualizacion timestamp with time zone,
  login character varying(15) NOT NULL,
  contrasenia character varying(15) NOT NULL,
  id_direccion_id integer,
  CONSTRAINT "mp_persona_pkey" PRIMARY KEY (id),
  CONSTRAINT "mp_persona_id_direccion_id_4407d371_fk_mp_direccion_id" FOREIGN KEY (id_direccion_id)
      REFERENCES "mp_direccion" (id) MATCH SImpLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT "mp_persona_correo_electronico_key" UNIQUE (correo_electronico),
  CONSTRAINT "mp_persona_login_key" UNIQUE (login)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "mp_persona"
  OWNER TO postgres;

-- Index: "mp_persona_9639f13b"

-- DROP INDEX "mp_persona_9639f13b";

CREATE INDEX "mp_persona_9639f13b"
  ON "mp_persona"
  USING btree
  (id_direccion_id);

-- Index: "mp_persona_correo_electronico_4792d6c6_like"

-- DROP INDEX "mp_persona_correo_electronico_4792d6c6_like";

CREATE INDEX "mp_persona_correo_electronico_4792d6c6_like"
  ON "mp_persona"
  USING btree
  (correo_electronico COLLATE pg_catalog."default" varchar_pattern_ops);

-- Index: "mp_persona_login_3484cd3f_like"

-- DROP INDEX "mp_persona_login_3484cd3f_like";

CREATE INDEX "mp_persona_login_3484cd3f_like"
  ON "mp_persona"
  USING btree
  (login COLLATE pg_catalog."default" varchar_pattern_ops);

-- Table: mp_producto

-- DROP TABLE mp_producto;

CREATE TABLE mp_producto
(
  id serial NOT NULL,
  estado character varying(30) NOT NULL,
  nombre character varying(50) NOT NULL,
  fecha_registro timestamp with time zone NOT NULL,
  fecha_actualizacion timestamp with time zone,
  calificacion_promedio integer,
  id_padre integer,
  id_direccion_id integer,
  tipo_producto_id integer,
  descripcion character varying(255) NOT NULL,
  precio double precision NOT NULL,
  id_usuario integer NOT NULL,
  cantidad_actual integer NOT NULL,
  cantidad_origen integer NOT NULL,
  CONSTRAINT mp_producto_pkey PRIMARY KEY (id),
  CONSTRAINT id_usuario FOREIGN KEY (id_usuario)
  REFERENCES mp_persona (id) MATCH SIMPLE
  ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT mp_producto_id_direccion_id_a68deeec_fk_mp_direccion_id FOREIGN KEY (id_direccion_id)
  REFERENCES mp_direccion (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT mp_producto_tipo_producto_id_4f6166cc_fk_mp_tipo_producto_id FOREIGN KEY (tipo_producto_id)
  REFERENCES mp_tipo_producto (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED
)
WITH (
OIDS=FALSE
);
ALTER TABLE mp_producto
OWNER TO igqcflmodfdggd;

-- Index: fki_id_usuario

-- DROP INDEX fki_id_usuario;

CREATE INDEX fki_id_usuario
ON mp_producto
USING btree
(id_usuario);

-- Index: mp_producto_9639f13b

-- DROP INDEX mp_producto_9639f13b;

CREATE INDEX mp_producto_9639f13b
ON mp_producto
USING btree
(id_direccion_id);

-- Index: mp_producto_ab4462f4

-- DROP INDEX mp_producto_ab4462f4;

CREATE INDEX mp_producto_ab4462f4
ON mp_producto
USING btree
(tipo_producto_id);




-- Table: "mp_galeria"

-- DROP TABLE "mp_galeria";

CREATE TABLE "mp_galeria"
(
  id serial NOT NULL,
  tipo character varying(15) NOT NULL,
  url character varying(1000) NOT NULL,
  descripcion character varying(255) ,
  producto_id integer,
  foto_principal integer,
  CONSTRAINT "mp_galeria_pkey" PRIMARY KEY (id),
  CONSTRAINT "mp_galeria_mp_producto_fk_producto_id" FOREIGN KEY (producto_id)
  REFERENCES "mp_producto" (id) MATCH SImpLE
  ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED

)
WITH (
  OIDS=FALSE
);
ALTER TABLE "mp_galeria"
  OWNER TO postgres;

  
-- Table: "mp_lista_valores"

-- DROP TABLE "mp_lista_valores";

CREATE TABLE "mp_lista_valores"
(
  id serial NOT NULL,
  tipo character varying(100) NOT NULL,
  codigo character varying(100) NOT NULL,
  valor character varying(100) NOT NULL,
  id_padre integer,
  CONSTRAINT "mp_lista_valores_pkey" PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "mp_lista_valores"
  OWNER TO postgres;


-- Table: "mp_orden"

-- DROP TABLE "mp_orden";

CREATE TABLE "mp_orden"
(
  id serial NOT NULL,
  estado character varying(30) NOT NULL,
  "precioTotal" double precision NOT NULL,
  id_cliente_id integer NOT NULL,
  CONSTRAINT "mp_orden_pkey" PRIMARY KEY (id),
  CONSTRAINT "mp_orden_id_cliente_id_8810b836_fk_mp_persona_id" FOREIGN KEY (id_cliente_id)
      REFERENCES "mp_persona" (id) MATCH SImpLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "mp_orden"
  OWNER TO postgres;

-- Index: "mp_orden_6d98a18e"

-- DROP INDEX "mp_orden_6d98a18e";

CREATE INDEX "mp_orden_6d98a18e"
  ON "mp_orden"
  USING btree
  (id_cliente_id);

-- Table: "mp_orden_item"

-- DROP TABLE "mp_orden_item";

CREATE TABLE "mp_orden_item"
(
  id serial NOT NULL,
  cantidad integer NOT NULL,
  id_orden_id integer NOT NULL,
  id_producto_id integer NOT NULL,
  CONSTRAINT "mp_orden_item_pkey" PRIMARY KEY (id),
  CONSTRAINT "mp_orden_item_id_orden_id_c0b33d83_fk_mp_orden_id" FOREIGN KEY (id_orden_id)
      REFERENCES "mp_orden" (id) MATCH SImpLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT "mp_orden_item_id_producto_id_29d154a5_fk_mp_producto_id" FOREIGN KEY (id_producto_id)
      REFERENCES "mp_producto" (id) MATCH SImpLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "mp_orden_item"
  OWNER TO postgres;

-- Index: "mp_orden_item_91524a00"

-- DROP INDEX "mp_orden_item_91524a00";

CREATE INDEX "mp_orden_item_91524a00"
  ON "mp_orden_item"
  USING btree
  (id_orden_id);

-- Index: "mp_orden_item_ebd2e856"

-- DROP INDEX "mp_orden_item_ebd2e856";

CREATE INDEX "mp_orden_item_ebd2e856"
  ON "mp_orden_item"
  USING btree
  (id_producto_id);




-- Table: "mp_transaccion"

-- DROP TABLE "mp_transaccion";

CREATE TABLE "mp_transaccion"
(
  id serial NOT NULL,
  medio_pago character varying(15) NOT NULL,
  fecha timestamp with time zone NOT NULL,
  estado character varying(30) NOT NULL,
  id_orden_id integer NOT NULL,
  CONSTRAINT "mp_transaccion_pkey" PRIMARY KEY (id),
  CONSTRAINT "mp_transaccion_id_orden_id_a09f0fdb_fk_mp_orden_id" FOREIGN KEY (id_orden_id)
      REFERENCES "mp_orden" (id) MATCH SImpLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "mp_transaccion"
  OWNER TO postgres;

-- Index: "mp_transaccion_91524a00"

-- DROP INDEX "mp_transaccion_91524a00";

CREATE INDEX "mp_transaccion_91524a00"
  ON "mp_transaccion"
  USING btree
  (id_orden_id);
  
  
  -- Table: "mp_calificacion"

-- DROP TABLE "mp_calificacion";

CREATE TABLE "mp_calificacion"
(
  id serial NOT NULL,
  calificacion integer NOT NULL,
  fecha timestamp with time zone NOT NULL,
  comentario character varying(255),
  id_cliente_id integer NOT NULL,
  id_producto_id integer NOT NULL,
  CONSTRAINT "mp_calificacion_pkey" PRIMARY KEY (id),
  CONSTRAINT "mp_calificacion_id_cliente_id_99ea2eda_fk_mp_persona_id" FOREIGN KEY (id_cliente_id)
      REFERENCES "mp_persona" (id) MATCH SImpLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT "mp_calificacion_id_producto_id_48e17d66_fk_mp_producto_id" FOREIGN KEY (id_producto_id)
      REFERENCES "mp_producto" (id) MATCH SImpLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "mp_calificacion"
  OWNER TO postgres;

-- Index: "mp_calificacion_6d98a18e"

-- DROP INDEX "mp_calificacion_6d98a18e";

CREATE INDEX "mp_calificacion_6d98a18e"
  ON "mp_calificacion"
  USING btree
  (id_cliente_id);

-- Index: "mp_calificacion_ebd2e856"

-- DROP INDEX "mp_calificacion_ebd2e856";

CREATE INDEX "mp_calificacion_ebd2e856"
  ON "mp_calificacion"
  USING btree
  (id_producto_id);
  
  -- Table: "mp_comentarios"

-- DROP TABLE "mp_comentarios";

CREATE TABLE "mp_comentarios"
(
  id serial NOT NULL,
  comentario character varying(255) NOT NULL,
  id_padre integer,
  fecha_registro timestamp with time zone NOT NULL,
  id_persona_id integer NOT NULL,
  CONSTRAINT "mp_comentarios_pkey" PRIMARY KEY (id),
  CONSTRAINT "mp_comentarios_id_persona_id_c9b15c64_fk_mp_persona_id" FOREIGN KEY (id_persona_id)
      REFERENCES "mp_persona" (id) MATCH SImpLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "mp_comentarios"
  OWNER TO postgres;

-- Index: "mp_comentarios_43a005b1"

-- DROP INDEX "mp_comentarios_43a005b1";

CREATE INDEX "mp_comentarios_43a005b1"
  ON "mp_comentarios"
  USING btree
  (id_persona_id);

  
-- Table: "mp_mensaje"

-- DROP TABLE "mp_mensaje";

CREATE TABLE "mp_mensaje"
(
  id serial NOT NULL,
  mensaje character varying(1000) NOT NULL,
  fecha_registro timestamp with time zone NOT NULL,
  id_padre integer,
  id_destinatario_id integer NOT NULL,
  id_remitente_id integer NOT NULL,
  CONSTRAINT "mp_mensaje_pkey" PRIMARY KEY (id),
  CONSTRAINT "mp_mensaje_id_destinatario_id_4c1c9d40_fk_mp_persona_id" FOREIGN KEY (id_destinatario_id)
      REFERENCES "mp_persona" (id) MATCH SImpLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT "mp_mensaje_id_remitente_id_9aee5536_fk_mp_persona_id" FOREIGN KEY (id_remitente_id)
      REFERENCES "mp_persona" (id) MATCH SImpLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "mp_mensaje"
  OWNER TO postgres;

-- Index: "mp_mensaje_88a52d1b"

-- DROP INDEX "mp_mensaje_88a52d1b";

CREATE INDEX "mp_mensaje_88a52d1b"
  ON "mp_mensaje"
  USING btree
  (id_destinatario_id);

-- Index: "mp_mensaje_da6b3ccd"

-- DROP INDEX "mp_mensaje_da6b3ccd";

CREATE INDEX "mp_mensaje_da6b3ccd"
  ON "mp_mensaje"
  USING btree
  (id_remitente_id);


-- Table: mp_preguntas

-- DROP TABLE mp_preguntas;

CREATE TABLE mp_preguntas
(
  id serial NOT NULL,
  pregunta text NOT NULL,
  id_padre integer,
  fecha_registro timestamp with time zone NOT NULL,
  id_persona_id integer,
  id_producto integer NOT NULL,
  respuesta text,
  CONSTRAINT mp_preguntas_pkey PRIMARY KEY (id),
  CONSTRAINT mp_preguntas_id_persona_id_c9b15c64_fk_mp_persona_id FOREIGN KEY (id_persona_id)
  REFERENCES mp_persona (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT mp_preguntas_id_producto_c9b15c64_fk_mp_persona_id FOREIGN KEY (id_producto)
  REFERENCES mp_producto (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED
)
WITH (
OIDS=FALSE
);
ALTER TABLE mp_preguntas
OWNER TO igqcflmodfdggd;

-- Index: mp_preguntas_43a005b1

-- DROP INDEX mp_preguntas_43a005b1;

CREATE INDEX mp_preguntas_43a005b1
ON mp_preguntas
USING btree
(id_persona_id);

-- Index: mp_preguntas_43a005b2

-- DROP INDEX mp_preguntas_43a005b2;

CREATE INDEX mp_preguntas_43a005b2
ON mp_preguntas
USING btree
(id_producto);