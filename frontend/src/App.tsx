import { useEffect, useMemo, useState } from "react";
import { BookingPanel } from "./components/BookingPanel";
import { SeatMap } from "./components/SeatMap";
import { bookSeats, getSeats, initializeTheater, type Seat } from "./services/api";
import "./styles.css";

const MAX_SELECTION = 6;

export default function App() {
  const [seats, setSeats] = useState<Seat[]>([]);
  const [selectedSeatIds, setSelectedSeatIds] = useState<number[]>([]);
  const [userName, setUserName] = useState("");
  const [message, setMessage] = useState("");
  const [isBusy, setIsBusy] = useState(true);

  const totalPrice = useMemo(
    () =>
      selectedSeatIds.reduce((total, seatId) => {
        const seat = seats.find((currentSeat) => currentSeat.id === seatId);
        return total + (seat?.price ?? 0);
      }, 0),
    [seats, selectedSeatIds]
  );

  async function loadSeats() {
    setIsBusy(true);
    setMessage("");
    try {
      setSeats(await getSeats());
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Unable to load seats");
    } finally {
      setIsBusy(false);
    }
  }

  useEffect(() => {
    void loadSeats();
  }, []);

  function toggleSeat(seat: Seat) {
    setMessage("");
    if (seat.booked) {
      return;
    }

    setSelectedSeatIds((currentSelection) => {
      if (currentSelection.includes(seat.id)) {
        return currentSelection.filter((seatId) => seatId !== seat.id);
      }

      if (currentSelection.length === MAX_SELECTION) {
        setMessage("You can select up to 6 seats per booking.");
        return currentSelection;
      }

      return [...currentSelection, seat.id];
    });
  }

  async function handleBuy() {
    setIsBusy(true);
    setMessage("");

    try {
      const response = await bookSeats(userName, selectedSeatIds);
      setMessage(`Booked ${response.seats.length} seats for ${response.userName}. Total: $${response.totalPrice}.`);
      setSelectedSeatIds([]);
      setSeats(await getSeats());
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Booking failed");
    } finally {
      setIsBusy(false);
    }
  }

  async function handleReset() {
    setIsBusy(true);
    setMessage("");

    try {
      setSeats(await initializeTheater());
      setSelectedSeatIds([]);
      setUserName("");
      setMessage("Theater has been reset.");
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Reset failed");
    } finally {
      setIsBusy(false);
    }
  }

  return (
    <main className="app-shell">
      <section className="topbar" aria-label="Theater overview">
        <div>
          <p className="eyebrow">Movie Theater</p>
          <h1>Seat Booking</h1>
        </div>
        <div className="tier-legend" aria-label="Pricing tiers">
          <span className="legend-item silver">Silver $100</span>
          <span className="legend-item gold">Gold $150</span>
          <span className="legend-item platinum">Platinum $200</span>
          <span className="legend-item booked">Booked</span>
        </div>
      </section>

      <section className="workspace">
        <SeatMap seats={seats} selectedSeatIds={selectedSeatIds} onToggleSeat={toggleSeat} />
        <BookingPanel
          bookingMessage={message}
          isBusy={isBusy}
          onBuy={handleBuy}
          onReset={handleReset}
          onUserNameChange={setUserName}
          seats={seats}
          selectedSeatIds={selectedSeatIds}
          userName={userName}
        />
      </section>

      <div className="mobile-total" aria-label="Mobile total">
        <span>Total</span>
        <strong>${totalPrice}</strong>
      </div>
    </main>
  );
}
