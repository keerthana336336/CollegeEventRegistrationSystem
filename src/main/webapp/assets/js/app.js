/* College Event Registration System - small helpers, no libraries. */
(function () {
  'use strict';

  // ----- Mobile menu -----
  var toggle = document.querySelector('.nav-toggle');
  var menu = document.getElementById('nav-menu');
  if (toggle && menu) {
    toggle.addEventListener('click', function () {
      var open = menu.classList.toggle('open');
      toggle.setAttribute('aria-expanded', open ? 'true' : 'false');
    });
  }

  // ----- Toast notifications: close button + auto-dismiss -----
  function dismiss(toast) {
    toast.classList.add('is-leaving');
    setTimeout(function () { if (toast.parentNode) toast.parentNode.removeChild(toast); }, 280);
  }
  document.querySelectorAll('.toast').forEach(function (toast) {
    var btn = toast.querySelector('.toast-close');
    if (btn) btn.addEventListener('click', function () { dismiss(toast); });
    setTimeout(function () { dismiss(toast); }, toast.classList.contains('toast-error') ? 9000 : 5500);
  });

  // ----- Confirmation dialog for <form data-confirm="..."> -----
  var confirmed = new WeakSet();
  document.addEventListener('submit', function (event) {
    var form = event.target;
    if (!form.matches || !form.matches('form[data-confirm]') || confirmed.has(form)) return;
    event.preventDefault();
    openConfirm(form);
  });

  function openConfirm(form) {
    var opener = document.activeElement;
    var backdrop = document.createElement('div');
    backdrop.className = 'modal-backdrop';
    var modal = document.createElement('div');
    modal.className = 'modal';
    modal.setAttribute('role', 'alertdialog');
    modal.setAttribute('aria-modal', 'true');
    modal.setAttribute('aria-labelledby', 'confirm-title');

    var title = document.createElement('h3');
    title.id = 'confirm-title';
    title.textContent = form.getAttribute('data-confirm-title') || 'Please confirm';
    var text = document.createElement('p');
    text.textContent = form.getAttribute('data-confirm');
    var actions = document.createElement('div');
    actions.className = 'form-actions';
    var cancel = document.createElement('button');
    cancel.type = 'button';
    cancel.className = 'btn btn-outline';
    cancel.textContent = 'Keep it';
    var ok = document.createElement('button');
    ok.type = 'button';
    ok.className = 'btn ' + (form.getAttribute('data-confirm-style') === 'primary' ? 'btn-primary' : 'btn-danger');
    ok.textContent = form.getAttribute('data-confirm-button') || 'Yes, continue';
    actions.appendChild(cancel);
    actions.appendChild(ok);
    modal.appendChild(title);
    modal.appendChild(text);
    modal.appendChild(actions);
    backdrop.appendChild(modal);
    document.body.appendChild(backdrop);
    cancel.focus();

    function close() {
      document.removeEventListener('keydown', onKey);
      document.body.removeChild(backdrop);
      if (opener && opener.focus) opener.focus();
    }
    function onKey(e) {
      if (e.key === 'Escape') close();
      if (e.key === 'Tab') { // keep focus inside the dialog
        e.preventDefault();
        (document.activeElement === cancel ? ok : cancel).focus();
      }
    }
    document.addEventListener('keydown', onKey);
    cancel.addEventListener('click', close);
    backdrop.addEventListener('click', function (e) { if (e.target === backdrop) close(); });
    ok.addEventListener('click', function () {
      confirmed.add(form);
      document.body.removeChild(backdrop);
      document.removeEventListener('keydown', onKey);
      form.submit();
    });
  }

  // ----- Password strength meter: <input data-strength="#meterId"> -----
  document.querySelectorAll('input[data-strength]').forEach(function (input) {
    var bar = document.querySelector(input.getAttribute('data-strength'));
    if (!bar) return;
    input.addEventListener('input', function () {
      var v = input.value, score = 0;
      if (v.length >= 8) score++;
      if (/[A-Za-z]/.test(v) && /\d/.test(v)) score++;
      if (v.length >= 12) score++;
      if (/[^A-Za-z0-9]/.test(v)) score++;
      var colors = ['#C0233F', '#C0233F', '#D98A00', '#1F6FFF', '#0C7A42'];
      bar.style.width = (v.length ? Math.max(1, score) * 25 : 0) + '%';
      bar.style.backgroundColor = colors[score];
    });
  });
})();
