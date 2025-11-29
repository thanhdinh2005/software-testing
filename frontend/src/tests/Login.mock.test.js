import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Login from "../components/Login";
import * as authService from "../services/authService";

jest.mock("../services/authService");

describe("Frontend Mocking", () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });
  
  test("Mock success response", async () => {
    authService.loginUser.mockResolvedValue({
      data: {
        token: "abc123"
      }
    });

    render(<MemoryRouter><Login /></MemoryRouter>);

    fireEvent.change(screen.getByTestId("username-input"), { target: { value: "admin" }});
    fireEvent.change(screen.getByTestId("password-input"), { target: { value: "admin123" }});
    fireEvent.click(screen.getByTestId("login-button"));

    await waitFor(() => {
        expect(authService.loginUser).toHaveBeenCalledTimes(1);
        expect(authService.loginUser).toHaveBeenCalledWith("admin", "admin123");
        expect(screen.getByTestId("login-message")).toHaveTextContent("Login successful");
    });
  });

  test("Mock Failed response", async () => {
    authService.loginUser.mockRejectedValue(new Error("Login Failed"));

    render(<MemoryRouter><Login /></MemoryRouter>);

    fireEvent.change(screen.getByTestId("username-input"), { target: { value: "wrong" }});
    fireEvent.change(screen.getByTestId("password-input"), { target: { value: "Wrong123" }});
    fireEvent.click(screen.getByTestId("login-button"));

    await waitFor(() => {
        expect(authService.loginUser).toHaveBeenCalledTimes(1);
        expect(screen.getByTestId("login-message")).toHaveTextContent("Login Failed");
    });
  });

});
