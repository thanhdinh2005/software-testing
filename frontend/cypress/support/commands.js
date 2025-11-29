const originalFn = Cypress.Commands.overwrite;

Cypress.Commands.overwrite('type', (originalFn, subject, str, options) => {
  return originalFn(subject, str, { delay: 100, ...options })
});
