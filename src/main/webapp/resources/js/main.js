document.addEventListener('DOMContentLoaded', function() {
    'use strict';
    console.log("main.js: DOMContentLoaded event fired. Full script executing.");

    const preloader = document.getElementById('preloader');
    if (preloader) {
        console.log("main.js: Preloader element found.");
        setTimeout(function() {
            console.log("main.js: Hiding preloader now.");
            preloader.style.display = 'none';
        }, 1000);
    } else {
        console.error("main.js: Preloader element with ID 'preloader' NOT found!");
    }

    const header = document.querySelector('.header-area');
    if(header){
        console.log("main.js: Header element found for sticky check.");
        window.addEventListener('scroll', function() {
            if (window.scrollY > 100) {
                header.classList.add('sticky');
            } else {
                header.classList.remove('sticky');
            }
        });
    } else {
        console.warn("main.js: Header element with class '.header-area' NOT found for sticky check.");
    }

    if (typeof AOS !== 'undefined') {
        console.log("main.js: AOS library found, initializing AOS.");
        AOS.init({
            duration: 1000,
            once: true,
            easing: 'ease-in-out'
        });
    } else {
        console.warn("main.js: AOS library not found. Animations on scroll will not work.");
    }

    if (typeof gsap !== 'undefined' && typeof ScrollTrigger !== 'undefined') {
        console.log("main.js: GSAP and ScrollTrigger found. Initializing GSAP animations.");

        if (document.querySelector('.hero-text')) {
            gsap.from('.hero-text h1', { y: 50, opacity: 0, duration: 1, delay: 0.5 });
            gsap.from('.hero-text p', { y: 50, opacity: 0, duration: 1, delay: 0.8 });
            gsap.from('.hero-text .hero-btn', { y: 50, opacity: 0, duration: 1, delay: 1.1, stagger: 0.2 });
        }

        gsap.utils.toArray('.section-title').forEach(title => {
            ScrollTrigger.create({
                trigger: title,
                start: "top 80%",
                onEnter: () => { gsap.fromTo(title, { y: 100, opacity: 0 }, { y: 0, opacity: 1, duration: 1 }); }
            });
        });

        gsap.utils.toArray('.feature-box').forEach((box, index) => {
            ScrollTrigger.create({
                trigger: box,
                start: "top 80%",
                onEnter: () => { gsap.fromTo(box, { y: 100, opacity: 0 }, { y: 0, opacity: 1, duration: 0.8, delay: index * 0.2 });}
            });
        });

        // DEBUG SKILL BARS
        console.log("main.js: Number of .skill-progress-bar elements found:", document.querySelectorAll('.skill-progress-bar').length);
        if (document.querySelector('.skill-progress-bar')) {
            const skillBars = document.querySelectorAll('.skill-progress-bar');
            console.log("main.js: .skill-progress-bar elements are present. Total:", skillBars.length);
            console.log("main.js: Is .skills-area element present?", document.querySelector('.skills-area') !== null);

            ScrollTrigger.create({
                trigger: '.skills-area',
                start: "top 80%",
                markers: true, // THÊM DÒNG NÀY ĐỂ DEBUG TRIGGER CỦA SCROLLTRIGGER
                onEnter: () => {
                    console.log("main.js: Skills area entered view, animating skill bars.");
                    skillBars.forEach(skill => {
                        const percent = skill.getAttribute('data-progress');
                        if(percent){
                            console.log("main.js: Animating skill:", skill, "to width:", percent);
                            gsap.to(skill, { width: percent, duration: 1.5, ease: "power3.out" });
                        } else {
                            console.warn("main.js: Skill bar missing data-progress attribute:", skill);
                        }
                    });
                },
                onLeaveBack: () => { // Optional: reset animation if needed when scrolling back up
                    console.log("main.js: Skills area left view (scrolling up). Resetting skill bars.");
                    skillBars.forEach(skill => {
                        gsap.to(skill, { width: '0%', duration: 0.5 });
                    });
                }
            });
        } else {
            console.warn("main.js: No elements with class '.skill-progress-bar' were found by querySelector.");
        }
        // END DEBUG SKILL BARS


        if (document.querySelector('.timeline-item')) {
            gsap.utils.toArray('.timeline-item').forEach((item, index) => {
                ScrollTrigger.create({
                    trigger: item,
                    start: "top 80%",
                    onEnter: () => { gsap.fromTo(item, { opacity: 0, x: index % 2 === 0 ? -100 : 100 }, { opacity: 1, x: 0, duration: 1 });}
                });
            });
        }

        if (document.querySelector('.testimonial-item')) {
            gsap.utils.toArray('.testimonial-item').forEach((item, index) => {
                ScrollTrigger.create({
                    trigger: item,
                    start: "top 80%",
                    onEnter: () => { gsap.fromTo(item, { opacity: 0, y: 50 }, { opacity: 1, y: 0, duration: 0.8, delay: index * 0.2 });}
                });
            });
        }

        if (document.querySelector('.company-box')) {
            gsap.utils.toArray('.company-box').forEach((box) => {
                ScrollTrigger.create({
                    trigger: box,
                    start: "top 80%",
                    onEnter: () => { gsap.fromTo(box, { opacity: 0, y: 50 }, { opacity: 1, y: 0, duration: 0.8 });}
                });
            });
        }

        if (document.querySelector('.about-img') && document.querySelector('.about-content')) {
            ScrollTrigger.create({
                trigger: '.about-img',
                start: "top 80%",
                onEnter: () => { gsap.fromTo('.about-img', { opacity: 0, x: -100 }, { opacity: 1, x: 0, duration: 1 });}
            });
            ScrollTrigger.create({
                trigger: '.about-content',
                start: "top 80%",
                onEnter: () => { gsap.fromTo('.about-content', { opacity: 0, x: 100 }, { opacity: 1, x: 0, duration: 1 });}
            });
        }
    } else {
        console.warn("main.js: GSAP or ScrollTrigger not found. GSAP animations will not work.");
    }

    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            const targetId = this.getAttribute('href');
            if (targetId && targetId.length > 1 && targetId.startsWith('#')) {
                const targetElement = document.querySelector(targetId);
                if (targetElement) {
                    e.preventDefault();
                    const offsetTop = targetElement.offsetTop;
                    const headerOffset = document.querySelector('.header-area.sticky') ? 80 : 100;
                    window.scrollTo({
                        top: offsetTop - headerOffset,
                        behavior: 'smooth'
                    });
                }
            } else if (targetId === '#') {
                e.preventDefault();
            }
        });
    });
    console.log("main.js: Event listeners for smooth scroll attached.");
});