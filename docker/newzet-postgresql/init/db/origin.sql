
CREATE TABLE IF NOT EXISTS category (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    image_url VARCHAR(255),
    emoji VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS newsletter (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    category_id UUID NOT NULL,
    domain VARCHAR(255) NOT NULL U NIQUE,
    mailing_list VARCHAR(255),
    priority INTEGER NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    detail VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    day_of_week VARCHAR(255),
    subscription_url VARCHAR(255) NOT NULL,
    color VARCHAR(255) NOT NULL DEFAULT 'DEFAULT',
    deleted_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS advertise (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    newsletter_id UUID NOT NULL
);

CREATE TABLE IF NOT EXISTS userinfo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    nickname VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS article (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    to_user_id UUID,
    from_name VARCHAR(255),
    from_domain VARCHAR(255),
    mailing_list VARCHAR(255),
    title VARCHAR(255),
    content_url VARCHAR(255),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    is_like BOOLEAN NOT NULL DEFAULT FALSE,
    is_share BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS subscription (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    newsletter_name VARCHAR(255) NOT NULL,
    newsletter_domain VARCHAR(255) NOT NULL,
    newsletter_mailing_list VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS user_category (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    category_id UUID NOT NULL
);
