package MyApp.panel;

import MyApp.building.Building;
import MyApp.elevator.Elevator;
import MyApp.kiosk.Kiosk;
import MyApp.misc.ElevatorStatus;
import sun.misc.JavaLangAccess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.stream.Collectors;

public class ControlPanel implements Panel {
    private static final long statusRefreshMiliseconds = 100;

    private final Building building;
    private final Hashtable<Elevator, Tuple<JLabel, JLabel>> labelsElevatorStatus = new Hashtable<>();
    private final Hashtable<Elevator, Tuple<JLabel, JLabel>> labelsElevatorQueue = new Hashtable<>();
    private final Hashtable<Kiosk, Tuple<JLabel, JLabel>> labelsKioskQueue = new Hashtable<>();
    private JFrame frmControlPanel;

    private JPanel panelWrapper;
    @SuppressWarnings("FieldCanBeLocal")
    private JPanel panelElevatorsInfo;
    @SuppressWarnings("FieldCanBeLocal")
    private JPanel panelKiosksQueuesWrapper;
    @SuppressWarnings("FieldCanBeLocal")
    private JLabel lblElevatorStatus;
    @SuppressWarnings("FieldCanBeLocal")
    private JLabel lblElevatorQueue;
    @SuppressWarnings("FieldCanBeLocal")
    private JLabel lblKioskQueues;
    private JPanel panelKiosksQueues;
    private JPanel panelElevatorQueue;
    private JPanel panelElevatorStatus;

    public ControlPanel(Building building) {
        this.building = building;
        this.frmControlPanel = new JFrame("Control Panel");

        setupForm();

        Thread threadControlPanelRefresh = new Thread(() -> {
            while (true) {
                this.building.getElevatorStatus().forEach(this::updateElevatorStatus);
                this.building.getElevators().forEach(this::updateElevatorQueue);
                this.building.getKiosks().forEach(this::updateKioskQueue);

                try {
                    Thread.sleep(statusRefreshMiliseconds);
                } catch (InterruptedException e) {
                    System.out.println("Control Panel refreshing interrupted");
                    break;
                }
            }
        }, "threadControlPanelRefresh");
        threadControlPanelRefresh.start();
    }

    private void setupForm() {
        frmControlPanel.setSize(400, 500);
        frmControlPanel.setPreferredSize(new Dimension(400, 500));
        frmControlPanel.setContentPane(this.panelWrapper);
        frmControlPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmControlPanel.pack();
    }

    @Override
    public void showInfo() {
        EventQueue.invokeLater(() -> {
            try {
                frmControlPanel.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void dismissInfo() {
        EventQueue.invokeLater(() -> {
            try {
                frmControlPanel.dispatchEvent(new WindowEvent(frmControlPanel, WindowEvent.WINDOW_CLOSING));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private synchronized void addElevatorStatus(ElevatorStatus elevatorStatus) {
        GridBagConstraints gbc;

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = this.labelsElevatorStatus.size();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblElevatorStatusTitle0 = new JLabel();
        lblElevatorStatusTitle0.setText(String.format("Elevator %s:", elevatorStatus.getElevator().getID()));
        panelElevatorStatus.add(lblElevatorStatusTitle0, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = this.labelsElevatorStatus.size();
        gbc.weightx = 5.0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblElevatorStatus0 = new JLabel();
        lblElevatorStatus0.setText(
                String.format(
                        "YPos = %.2f m, Spd = %.2f m/s, Acc = %.2f m/s/s",
                        elevatorStatus.getYPosition(),
                        elevatorStatus.getVelocity(),
                        elevatorStatus.getAcceleration()
                )
        );
        panelElevatorStatus.add(lblElevatorStatus0, gbc);

        this.labelsElevatorStatus.put(elevatorStatus.getElevator(), new Tuple<>(lblElevatorStatusTitle0, lblElevatorStatus0));
    }

    private synchronized void updateElevatorStatus(ElevatorStatus elevatorStatus) {
        Tuple<JLabel, JLabel> labels = this.labelsElevatorStatus.get(elevatorStatus.getElevator());

        if (labels == null) {
            this.addElevatorStatus(elevatorStatus);
            return;
        }

        labels.y.setText(
                String.format(
                        "YPos = %.2f m, Spd = %.2f m/s, Acc = %.2f m/s/s",
                        elevatorStatus.getYPosition(),
                        elevatorStatus.getVelocity(),
                        elevatorStatus.getAcceleration()
                )
        );
    }

    private synchronized void addElevatorQueue(Elevator elevator) {
        GridBagConstraints gbc;

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = this.labelsElevatorQueue.size();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblElevatorQueueTitle0 = new JLabel();
        lblElevatorQueueTitle0.setText(
                String.format("Elevator %s:", elevator.getID())
        );
        panelElevatorQueue.add(lblElevatorQueueTitle0, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = this.labelsElevatorQueue.size();
        gbc.weightx = 5.0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblElevatorQueue0 = new JLabel();
        lblElevatorQueue0.setText(
                String.format(
                        "[%s]",
                        String.join(", ", elevator.getQueue().values())
                )
        );
        panelElevatorQueue.add(lblElevatorQueue0, gbc);

        this.labelsElevatorQueue.put(elevator, new Tuple<>(lblElevatorQueueTitle0, lblElevatorQueue0));
    }

    private synchronized void updateElevatorQueue(Elevator elevator) {
        Tuple<JLabel, JLabel> labels = this.labelsElevatorQueue.get(elevator);

        if (labels == null) {
            this.addElevatorQueue(elevator);
            return;
        }

        String[] floorNames = building.getFloorNames();

        labels.y.setText(
                String.format(
                        "[%s]",
                        String.join(", ", elevator.getQueue().keySet().stream().map(i -> floorNames[i]).collect(Collectors.toList()))
                )
        );
    }

    private synchronized void addKioskQueue(Kiosk kiosk) {
        GridBagConstraints gbc;

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = this.labelsKioskQueue.size();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblKioskFloor0 = new JLabel();
        lblKioskFloor0.setText(
                String.format("Floor %s:", kiosk.getFloor().getName())
        );
        panelKiosksQueues.add(lblKioskFloor0, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = this.labelsKioskQueue.size();
        gbc.weightx = 5.0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblKioskQueue0 = new JLabel();
        lblKioskQueue0.setText(
                String.format(
                        "[%s]",
                        String.join(", ", kiosk.getQueue().values())
                )
        );
        panelKiosksQueues.add(lblKioskQueue0, gbc);

        this.labelsKioskQueue.put(kiosk, new Tuple<>(lblKioskFloor0, lblKioskQueue0));
    }

    private synchronized void updateKioskQueue(Kiosk kiosk) {
        Tuple<JLabel, JLabel> labels = this.labelsKioskQueue.get(kiosk);

        if (labels == null) {
            this.addKioskQueue(kiosk);
            return;
        }

        labels.y.setText(
                String.format(
                        "[%s]",
                        String.join(", ", kiosk.getQueue().values())
                )
        );
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelWrapper = new JPanel();
        panelWrapper.setLayout(new GridBagLayout());
        panelElevatorsInfo = new JPanel();
        panelElevatorsInfo.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelWrapper.add(panelElevatorsInfo, gbc);
        lblElevatorStatus = new JLabel();
        lblElevatorStatus.setFont(new Font(lblElevatorStatus.getFont().getName(), lblElevatorStatus.getFont().getStyle(), 20));
        lblElevatorStatus.setText("Elevator Status");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelElevatorsInfo.add(lblElevatorStatus, gbc);
        panelElevatorStatus = new JPanel();
        panelElevatorStatus.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelElevatorsInfo.add(panelElevatorStatus, gbc);
        lblElevatorQueue = new JLabel();
        lblElevatorQueue.setFont(new Font(lblElevatorQueue.getFont().getName(), lblElevatorQueue.getFont().getStyle(), 20));
        lblElevatorQueue.setText("Elevator Queue");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelElevatorsInfo.add(lblElevatorQueue, gbc);
        panelElevatorQueue = new JPanel();
        panelElevatorQueue.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelElevatorsInfo.add(panelElevatorQueue, gbc);
        panelKiosksQueuesWrapper = new JPanel();
        panelKiosksQueuesWrapper.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelWrapper.add(panelKiosksQueuesWrapper, gbc);
        lblKioskQueues = new JLabel();
        lblKioskQueues.setFont(new Font(lblKioskQueues.getFont().getName(), lblKioskQueues.getFont().getStyle(), 20));
        lblKioskQueues.setText("Kiosk Queue:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelKiosksQueuesWrapper.add(lblKioskQueues, gbc);
        panelKiosksQueues = new JPanel();
        panelKiosksQueues.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelKiosksQueuesWrapper.add(panelKiosksQueues, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelWrapper;
    }
}

class Tuple<X, Y> {
    final X x;
    final Y y;

    Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}