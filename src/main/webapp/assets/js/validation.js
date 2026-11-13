/* Friendly client-side validation. The server validates everything again, so this is only a convenience.
   Opt in with <form class="js-validate" novalidate>. Extra rules via data attributes:
     data-match="#otherInput"   value must equal the other input (password confirmation)
     data-after="#otherInput"   time must be later than the other time input
     data-before-event="#date,#start"  datetime-local must not be after date + start time */
(function () {
  'use strict';

  function messageFor(input) {
    var v = input.validity;
    if (v.valueMissing) return 'This field is required.';
    if (v.typeMismatch && input.type === 'email') return 'Enter a valid e-mail address.';
    if (v.tooShort) return 'Use at least ' + input.minLength + ' characters.';
    if (v.tooLong) return 'Use at most ' + input.maxLength + ' characters.';
    if (v.rangeUnderflow) return 'The smallest allowed value is ' + input.min + '.';
    if (v.rangeOverflow) return 'The largest allowed value is ' + input.max + '.';
    if (v.patternMismatch) return input.getAttribute('data-pattern-message') || 'Please match the requested format.';
    return input.validationMessage;
  }

  function custom(input, form) {
    input.setCustomValidity('');
    if (!input.value) return;
    var sel = input.getAttribute('data-match');
    if (sel) {
      var other = form.querySelector(sel);
      if (other && other.value !== input.value) input.setCustomValidity('The two values do not match.');
    }
    sel = input.getAttribute('data-after');
    if (sel) {
      var first = form.querySelector(sel);
      if (first && first.value && input.value <= first.value) input.setCustomValidity('This time must be after the start time.');
    }
    sel = input.getAttribute('data-before-event');
    if (sel) {
      var parts = sel.split(',');
      var d = form.querySelector(parts[0]), t = form.querySelector(parts[1]);
      if (d && t && d.value && t.value && input.value > d.value + 'T' + t.value) {
        input.setCustomValidity('The deadline must be before the event starts.');
      }
    }
    if (input.getAttribute('data-password') !== null) {
      if (input.value.length < 8 || !/[A-Za-z]/.test(input.value) || !/\d/.test(input.value)) {
        input.setCustomValidity('Use at least 8 characters with a letter and a number.');
      }
    }
  }

  function show(input) {
    var field = input.closest('.field');
    if (!field) return;
    var note = field.querySelector('.field-error.js-error');
    if (input.validity.valid) {
      field.classList.remove('has-error');
      if (note) note.remove();
      input.removeAttribute('aria-invalid');
      return;
    }
    field.classList.add('has-error');
    input.setAttribute('aria-invalid', 'true');
    if (!note) {
      note = document.createElement('div');
      note.className = 'field-error js-error';
      note.setAttribute('role', 'alert');
      field.appendChild(note);
    }
    note.textContent = messageFor(input);
  }

  document.querySelectorAll('form.js-validate').forEach(function (form) {
    var inputs = form.querySelectorAll('input, select, textarea');
    function checkAll() {
      inputs.forEach(function (i) { custom(i, form); });
    }
    inputs.forEach(function (input) {
      input.addEventListener('blur', function () { checkAll(); show(input); });
      input.addEventListener('input', function () {
        checkAll();
        if (input.closest('.field') && input.closest('.field').classList.contains('has-error')) show(input);
        // linked fields (confirm password, end time, deadline) may change state too
        inputs.forEach(function (o) { if (o !== input && o.closest('.field') && o.closest('.field').classList.contains('has-error')) show(o); });
      });
      input.addEventListener('change', function () { checkAll(); show(input); });
    });
    form.addEventListener('submit', function (event) {
      checkAll();
      if (!form.checkValidity()) {
        event.preventDefault();
        var firstBad = null;
        inputs.forEach(function (i) { show(i); if (!firstBad && !i.validity.valid) firstBad = i; });
        if (firstBad) firstBad.focus();
      }
    });
  });
})();
