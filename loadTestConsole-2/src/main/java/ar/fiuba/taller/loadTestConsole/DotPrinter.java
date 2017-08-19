package ar.fiuba.taller.loadTestConsole;

import ar.fiuba.taller.utils.TerminateSignal;

public class DotPrinter implements Runnable {
	private TerminateSignal signal;

	public DotPrinter(TerminateSignal signal) {
		this.signal = signal;
	}

	public void run() {
		while (!signal.hasTerminate()) {
			System.out.print(".");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
