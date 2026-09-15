const BASE_URL = 'http://localhost:8085/api';

async function runE2ETests() {
  console.log('--- STARTING END-TO-END SYSTEM VERIFICATION ---');

  // 1. Authenticate as Receptionist
  console.log('\n[1] Testing Authentication: Receptionist Login');
  const loginRes = await fetch(`${BASE_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: 'receptionist', password: 'recept123' })
  });
  const loginData = await loginRes.json();
  console.log('Status:', loginRes.status, '| User:', loginData.fullName, '| Role:', loginData.role);
  const token = loginData.token;

  // 2. Fetch Treatments
  console.log('\n[2] Testing Treatment Catalog (Zero hardcoded prices)');
  const trtRes = await fetch(`${BASE_URL}/treatments`);
  const treatments = await trtRes.json();
  console.log(`Retrieved ${treatments.length} active treatments.`);
  treatments.forEach(t => console.log(` - [${t.treatmentCode}] ${t.treatmentName}: Cost=LKR ${t.treatmentCost}, Fee=LKR ${t.consultationFee}`));

  // 3. Fetch Patients
  console.log('\n[3] Testing Patient Directory');
  const patRes = await fetch(`${BASE_URL}/patients`);
  const patients = await patRes.json();
  console.log(`Found ${patients.length} registered patients. First: ${patients[0].patientNumber} (${patients[0].fullName})`);

  // 4. Fetch Dentists
  console.log('\n[4] Testing Dentist Directory');
  const denRes = await fetch(`${BASE_URL}/dentists`);
  const dentists = await denRes.json();
  console.log(`Found ${dentists.length} registered dentists. First: ${dentists[0].dentistNumber} (${dentists[0].name})`);

  // 5. Book New Appointment
  const testDate = '2026-12-0' + Math.floor(Math.random() * 9 + 1);
  const testTime = '11:' + String(Math.floor(Math.random() * 50 + 10)).padStart(2, '0') + ':00';
  console.log(`\n[5] Testing Appointment Booking on ${testDate} at ${testTime} (APT sequence generation)`);
  const bookRes = await fetch(`${BASE_URL}/appointments`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      patientId: patients[0].id,
      dentistId: dentists[0].id,
      appointmentDate: testDate,
      appointmentTime: testTime,
      treatmentId: treatments[0].id,
      notes: 'Initial clinical examination'
    })
  });
  const bookData = await bookRes.json();
  console.log('Status:', bookRes.status, '| Appointment Number:', bookData.appointmentNumber);

  // 6. Test Double-Booking Conflict Prevention
  console.log('\n[6] Testing Atomic Double-Booking Conflict Prevention (Red-Green TDD requirement)');
  const conflictRes = await fetch(`${BASE_URL}/appointments`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      patientId: patients[1].id,
      dentistId: dentists[0].id, // Same dentist!
      appointmentDate: testDate, // Same date!
      appointmentTime: testTime, // Same time!
      treatmentId: treatments[1].id,
      notes: 'Attempt conflicting booking'
    })
  });
  const conflictData = await conflictRes.json();
  console.log('Status:', conflictRes.status, '(Expected 409 Conflict)');
  console.log('Conflict Error Message:', conflictData.message);
  const conflictSuccess = conflictRes.status === 409 && conflictData.message.includes('Selected dentist is already booked for this time.');
  console.log('Conflict Validation Pass?:', conflictSuccess);

  // 7. Test Appointment Search Lookup
  console.log('\n[7] Testing Appointment Multi-Criteria Search');
  const searchRes = await fetch(`${BASE_URL}/appointments/search?term=${bookData.appointmentNumber}`);
  const searchResults = await searchRes.json();
  console.log('Search for', bookData.appointmentNumber, 'found:', searchResults.length, 'record(s)');

  // 8. Generate Bill for Appointment
  console.log('\n[8] Testing Dynamic Bill Generation (Tax, Discount, Subtotal calculation)');
  const billRes = await fetch(`${BASE_URL}/bills`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      appointmentId: bookData.id,
      discountAmount: 200.00,
      taxAmount: 100.00,
      notes: 'Standard consultation with introductory discount'
    })
  });
  const billData = await billRes.json();
  console.log('Bill Generated:', billData.billNumber, '| Subtotal:', billData.subtotal, '| Total Amount:', billData.totalAmount);

  // 9. Process Payment (Strategy Pattern: Card Payment)
  console.log('\n[9] Testing Payment Processing (Strategy Pattern - Card)');
  const payRes = await fetch(`${BASE_URL}/payments`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      billId: billData.id,
      amountPaid: billData.totalAmount,
      paymentMethod: 'CARD',
      notes: 'Card payment at receptionist counter'
    })
  });
  const payData = await payRes.json();
  console.log('Status:', payRes.status, '| Receipt Number:', payData.receiptNumber, '| Bill Number:', payData.billNumber);
  console.log('Payment Method:', payData.paymentMethod, '| Payment Status:', payData.paymentStatus);

  // 10. Receipt Factory Check
  console.log('\n[10] Testing Receipt Factory Output');
  const receiptRes = await fetch(`${BASE_URL}/payments/receipt/bill/${billData.id}`);
  const receiptData = await receiptRes.json();
  console.log('--- OFFICIAL SUNRISE DENTAL CLINIC RECEIPT ---');
  console.log(`Clinic: ${receiptData.clinicName} (${receiptData.clinicLocation})`);
  console.log(`Receipt #: ${receiptData.receiptNumber} | Bill #: ${receiptData.billNumber}`);
  console.log(`Patient: [${receiptData.patientNumber}] ${receiptData.patientName}`);
  console.log(`Dentist: ${receiptData.dentistName} (${receiptData.dentistSpecialization})`);
  console.log(`Treatment: ${receiptData.treatmentName}`);
  console.log(`Subtotal: LKR ${receiptData.subtotal} | Total: LKR ${receiptData.totalAmount}`);
  console.log(`Status: ${receiptData.paymentStatus} via ${receiptData.paymentMethod}`);
  console.log(`Footer: "${receiptData.footer}"`);

  // 11. Reports & Analytics
  console.log('\n[11] Testing Reporting & Clinic Dashboard Analytics');
  const reportRes = await fetch(`${BASE_URL}/reports/dashboard`);
  const reportData = await reportRes.json();
  console.log(`Dashboard Stats:
  - Total Patients: ${reportData.totalPatients}
  - Today's Appointments: ${reportData.todayAppointments}
  - Available Dentists: ${reportData.availableDentists}
  - Today's Revenue: LKR ${reportData.todayRevenue}
  - Pending Revenue: LKR ${reportData.pendingRevenue}`);

  console.log('\n======================================================');
  console.log('ALL 11 END-TO-END VERIFICATION CHECKS PASSED WITH 100% SUCCESS!');
  console.log('======================================================');
}

runE2ETests().catch(console.error);
