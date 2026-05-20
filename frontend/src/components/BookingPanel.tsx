import { RotateCcw, TicketCheck } from "lucide-react";
import type { Seat } from "../services/api";

type BookingPanelProps = {
  seats: Seat[];
  selectedSeatIds: number[];
  userName: string;
  bookingMessage: string;
  isBusy: boolean;
  onUserNameChange: (value: string) => void;
  onBuy: () => void;
  onReset: () => void;
};

const tierLabels = {
  SILVER: "Silver",
  GOLD: "Gold",
  PLATINUM: "Platinum"
};

export function BookingPanel({
  seats,
  selectedSeatIds,
  userName,
  bookingMessage,
  isBusy,
  onUserNameChange,
  onBuy,
  onReset
}: BookingPanelProps) {
  const selectedSeats = selectedSeatIds
    .map((seatId) => seats.find((seat) => seat.id === seatId))
    .filter((seat): seat is Seat => Boolean(seat));
  const totalPrice = selectedSeats.reduce((total, seat) => total + seat.price, 0);
  const bookedCount = seats.filter((seat) => seat.booked).length;
  const canBuy = selectedSeats.length > 0 && userName.trim().length > 0 && !isBusy;

  return (
    <aside className="booking-panel" aria-label="Booking summary">
      <div className="summary-cards">
        <div className="metric">
          <span>Selected</span>
          <strong>{selectedSeats.length}/6</strong>
        </div>
        <div className="metric">
          <span>Booked</span>
          <strong>{bookedCount}</strong>
        </div>
        <div className="metric">
          <span>Total</span>
          <strong>${totalPrice}</strong>
        </div>
      </div>

      <label className="field">
        <span>Name</span>
        <input
          aria-label="User name"
          autoComplete="name"
          onChange={(event) => onUserNameChange(event.target.value)}
          placeholder="Enter buyer name"
          type="text"
          value={userName}
        />
      </label>

      <div className="selected-list" aria-label="Selected seats">
        {selectedSeats.length === 0 ? (
          <p>No seats selected</p>
        ) : (
          selectedSeats.map((seat) => (
            <div className="selected-row" key={seat.id}>
              <span>Seat {seat.id}</span>
              <span>
                {tierLabels[seat.tier]} ${seat.price}
              </span>
            </div>
          ))
        )}
      </div>

      {bookingMessage ? (
        <div className="status-message" role="status">
          {bookingMessage}
        </div>
      ) : null}

      <div className="actions">
        <button className="primary-button" disabled={!canBuy} onClick={onBuy} type="button">
          <TicketCheck size={18} aria-hidden="true" />
          Buy
        </button>
        <button className="ghost-button" disabled={isBusy} onClick={onReset} type="button">
          <RotateCcw size={18} aria-hidden="true" />
          Reset
        </button>
      </div>
    </aside>
  );
}
