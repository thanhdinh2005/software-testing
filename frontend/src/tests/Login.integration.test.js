import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import React from "react";

import * as authService from "../services/authService";

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

jest.mock("../services/authService");
import Login from "../components/Login";

describe("Login Component Integration Tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test("TC1: Hien thi loi khi submit form rong", async () => {
    render(<Login />);

    fireEvent.click(screen.getByTestId("login-button"));

    await waitFor(() => {
      expect(screen.getByTestId("username-error")).toBeInTheDocument();
      expect(screen.getByTestId("password-error")).toBeInTheDocument();
    });
  });

  test("TC2: Goi API khi submit form hop le", async () => {
    authService.loginUser.mockResolvedValue({
      data: {
        token: "abc123"
      }
    });
    render(<Login />);

    fireEvent.change(screen.getByTestId("username-input"), {
      target: { value: "testuser" },
    });
    fireEvent.change(screen.getByTestId("password-input"), {
      target: { value: "Test123" },
    });
    fireEvent.click(screen.getByTestId("login-button"));

    await waitFor(() => {
        expect(authService.loginUser).toHaveBeenCalledWith("testuser", "Test123");
        // luu vao localStorage
        expect(localStorage.getItem("token")).toBe("abc123");
        expect(localStorage.getItem("username")).toBe("testuser");
        // bao dang nhap thanh cong
        expect(screen.getByTestId("login-message")).toHaveTextContent("Login successful");
        // chuyen trang
        expect(mockNavigate).toHaveBeenCalledWith("/dashboard");
    });
  });

  test("TC3: Hien thi loi khi dang nhap that bai ", async () => {
    authService.loginUser.mockRejectedValue(new Error("Login failed"));
    render(<Login />);

    fireEvent.change(screen.getByTestId("username-input"), {
      target: { value: "wrong" },
    });
    fireEvent.change(screen.getByTestId("password-input"), {
      target: { value: "T123456" },
    });
    fireEvent.click(screen.getByTestId("login-button"));

    await waitFor(() => {
      expect(screen.getByTestId("login-message")).toHaveTextContent("Login Failed");
    });
  });
});
