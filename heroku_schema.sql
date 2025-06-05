-- Bảng blog_posts
CREATE TABLE blog_posts (
                            id SERIAL PRIMARY KEY,
                            title VARCHAR(255) NOT NULL,
                            content TEXT,
                            summary TEXT,
                            author VARCHAR(100),
                            created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                            modified_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP, -- ON UPDATE CURRENT_TIMESTAMP của MySQL cần trigger trong PG
                            image_url VARCHAR(255),
                            category VARCHAR(100),
                            tags TEXT,
                            status VARCHAR(20) DEFAULT 'draft'
);

-- Bảng comments
CREATE TABLE comments (
                          id SERIAL PRIMARY KEY,
                          blog_post_id INT NOT NULL,
                          parent_comment_id INT,
                          author_name VARCHAR(100) NOT NULL,
                          author_email VARCHAR(100),
                          content TEXT NOT NULL,
                          created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          status VARCHAR(20) DEFAULT 'approved',
                          CONSTRAINT fk_comments_blog_post FOREIGN KEY (blog_post_id) REFERENCES blog_posts (id) ON DELETE CASCADE,
                          CONSTRAINT fk_comments_parent_comment FOREIGN KEY (parent_comment_id) REFERENCES comments (id) ON DELETE CASCADE
);

-- Bảng contact_messages
CREATE TABLE contact_messages (
                                  id SERIAL PRIMARY KEY,
                                  name VARCHAR(100) NOT NULL,
                                  email VARCHAR(100) NOT NULL,
                                  subject VARCHAR(255),
                                  message TEXT NOT NULL,
                                  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                  status VARCHAR(20) DEFAULT 'new'
);

CREATE TABLE profile (
                         id INT PRIMARY KEY DEFAULT 1, -- Nếu bạn luôn muốn ID là 1
                         name VARCHAR(100) NOT NULL,
                         position VARCHAR(100),
                         company_name VARCHAR(255),
                         company_tax_id VARCHAR(50),
                         company_address VARCHAR(255),
                         phone_number VARCHAR(20),
                         email VARCHAR(100) NOT NULL,
                         bio TEXT,
                         photo_url VARCHAR(255),
                         CONSTRAINT profile_id_must_be_one CHECK (id = 1) -- Giữ lại ràng buộc nếu bạn muốn
);

-- Bảng educations
CREATE TABLE educations (
                            id SERIAL PRIMARY KEY,
                            profile_id INT DEFAULT 1, -- Giả sử profile_id luôn là 1
                            school_name VARCHAR(255) NOT NULL,
                            degree VARCHAR(100),
                            field_of_study VARCHAR(100),
                            start_year VARCHAR(10),
                            end_year VARCHAR(10),
                            description TEXT,
                            CONSTRAINT fk_educations_profile FOREIGN KEY (profile_id) REFERENCES profile (id) ON DELETE CASCADE
);

-- Bảng experiences
CREATE TABLE experiences (
                             id SERIAL PRIMARY KEY,
                             profile_id INT DEFAULT 1, -- Giả sử profile_id luôn là 1
                             company_name VARCHAR(255) NOT NULL,
                             position VARCHAR(100) NOT NULL,
                             start_date DATE,
                             end_date DATE,
                             description_responsibilities TEXT,
                             CONSTRAINT fk_experiences_profile FOREIGN KEY (profile_id) REFERENCES profile (id) ON DELETE CASCADE
);

-- Bảng projects
CREATE TABLE projects (
                          id SERIAL PRIMARY KEY,
                          title VARCHAR(255) NOT NULL,
                          description TEXT,
                          client VARCHAR(100),
                          location VARCHAR(255),
                          start_date DATE,
                          end_date DATE,
                          image_url VARCHAR(255),
                          category VARCHAR(100),
                          status VARCHAR(50),
                          link VARCHAR(255)
);

-- Bảng skills
CREATE TABLE skills (
                        id SERIAL PRIMARY KEY,
                        profile_id INT DEFAULT 1, -- Giả sử profile_id luôn là 1
                        name VARCHAR(100) NOT NULL,
                        level INT DEFAULT 0,
                        category VARCHAR(100),
                        CONSTRAINT fk_skills_profile FOREIGN KEY (profile_id) REFERENCES profile (id) ON DELETE CASCADE
);

-- Bảng testimonials
CREATE TABLE testimonials (
                              id SERIAL PRIMARY KEY,
                              client_name VARCHAR(100) NOT NULL,
                              client_position_company VARCHAR(255),
                              quote_text TEXT NOT NULL,
                              client_image_url VARCHAR(255),
                              display_order INT DEFAULT 0
);

-- Bảng users
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password_hash VARCHAR(100) NOT NULL, -- Xem xét tăng độ dài nếu hash của bạn dài hơn
                       full_name VARCHAR(100),
                       role VARCHAR(20) DEFAULT 'admin',
                       created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.modified_date = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_blog_posts_modtime
    BEFORE UPDATE ON blog_posts
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_column();