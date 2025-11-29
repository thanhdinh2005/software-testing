import { validateUsername, validatePassword } from "../utils/validation";

describe("Login Validation Tests", () => {
  test("TC1: Username rong - nen tra ve loi", () => {
    expect(validateUsername("")).toBe("Ten dang nhap khong duoc de trong");
  });

  test("TC2: Username qua ngan - nen tra ve loi", () => {
    expect(validateUsername("ab")).toBe("Ten dang nhap phai co it nhat 3 ky tu");
  });

  test("TC3: Username qua dai - nen tra ve loi", () => {
    const longUsername = "c".repeat(51);
    expect(validateUsername(longUsername)).toBe("Ten dang nhap khong duoc vuot qua 50 ky tu");
  });

  test("TC4: Username chua ky tu dac biet khong hop le - nen tra ve loi", () => {
    expect(validateUsername("abc@")).toBe("Ten dang nhap chi duoc chua chu, so va ._-");
  });

  test("TC5: Username hop le - khong co loi", () => {
    expect(validateUsername("user_123")).toBe("");
  });

  test("TC6: Password rong - nen tra ve loi", () => {
    expect(validatePassword("")).toBe("Mat khau khong duoc de trong");
  });

  test("TC7: Password qua ngan - nen tra ve loi", () => {
    expect(validatePassword("123")).toBe("Mat khau phai co it nhat 6 ky tu");
  });

  test("TC8: Password qua dai - nen tra ve loi", () => {
    const longPassword = "a".repeat(101);
    expect(validatePassword(longPassword)).toBe("Mat khau khong duoc vuot qua 100 ky tu");
  });

  test("TC9: Password khong co chu hoac so - nen tra ve loi", () => {
    expect(validatePassword("abcdef")).toBe("Mat khau phai co ca chu va so");
  });

  test("TC10: Password hop le - khong co loi", () => {
    expect(validatePassword("Test123")).toBe("");
  });
});
