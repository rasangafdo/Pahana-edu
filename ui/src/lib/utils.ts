 
import { getCustomerById } from "@/services/customerService";
import { Customer } from "@/types/Customer";
import { SaleItem } from "@/types/SaleItem";
import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}


export const formatCurrency = (amount: number): string => {
  return `LKR  ${amount.toLocaleString('en-LK', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
};

export const handlePrint = async (
  customerId: number,
  saleItems: SaleItem[],
  subTotal: number,
  totalDiscount: number,
  totalAmount: number,
  paid: number,
  balance: number,
  toast

) => {
  let customer:Customer;
  
  
  try {
    customer = await getCustomerById(customerId);
    if (!customer) throw new Error("Customer not found");
  } catch (error) {
    console.error(error);
    toast({
          title: "Error",
          description: "Failed to fetch customer",
          variant: "destructive",
        });
    return; // exit early
  }
  
  const printContent = `
    <html>
      <head>
        <title>Sales Invoice</title>
        <style>
          body {
            font-family: Arial, sans-serif;
            padding: 10px;
            width: 80mm; /* Receipt size for POS printers */
          }
          h2, h3 {
            text-align: center;
            margin: 5px 0;
          }
          .divider {
            border-top: 1px dashed #000;
            margin: 10px 0;
          }
          table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 5px;
            font-size: 14px;
          }
          th, td {
            text-align: left;
            padding: 4px 2px;
          }
          th {
            border-bottom: 1px solid #000;
          }
          .right {
            text-align: right;
          }
          .totals {
            margin-top: 10px;
            font-weight: bold;
            font-size: 14px;
          }
          .footer {
            text-align: center;
            margin-top: 15px;
            font-size: 13px;
          }
        </style>
      </head>
      <body>
        <h2>PAHANA EDU MANAGEMENT SYSTEM</h2>
        <h3>SALES INVOICE</h3>
        <div class="divider"></div>

        <p><strong>Customer:</strong> ${customer.name}</p>
        <p><strong>Phone:</strong> ${customer.telephone}</p>
        <p><strong>Address:</strong> ${customer.address}</p>

        <p><strong>Date:</strong> ${new Date().toLocaleDateString()}</p>
        <p><strong>Time:</strong> ${new Date().toLocaleTimeString()}</p>

        <div class="divider"></div>
        <h4>Items:</h4>
        <table>
          <thead>
            <tr>
              <th>Item</th>
              <th class="right">Qty</th>
              <th class="right">Price</th>
              <th class="right">Total</th>
            </tr>
          </thead>
          <tbody>
            ${saleItems
              .map(
                (item) => `
                <tr>
                  <td>${item.item.name}</td>
                  <td class="right">${item.qty}</td>
                  <td class="right">LKR ${item.item.unitPrice.toFixed(2)}</td>
                  <td class="right">LKR ${item.itemTotal.toFixed(2)}</td>
                </tr>
                ${
                  item.discountAmount > 0
                    ? `<tr>
                        <td colspan="3" style="text-align:right;">Discount:</td>
                        <td class="right">- LKR ${item.discountAmount.toFixed(
                          2
                        )}</td>
                      </tr>`
                    : ""
                }
              `
              )
              .join("")}
          </tbody>
        </table>

        <div class="divider"></div>
        <div class="totals">
          <p>Subtotal: LKR ${subTotal.toFixed(2)}</p>
          ${
            totalDiscount > 0
              ? `<p>Total Discount: LKR ${totalDiscount.toFixed(2)}</p>`
              : ""
          }
          <p>TOTAL AMOUNT: LKR ${totalAmount.toFixed(2)}</p>
          <p>PAID: LKR ${paid.toFixed(2)}</p>
          <p>BALANCE: LKR ${balance.toFixed(2)}</p>
        </div>

        <div class="divider"></div>
        <div class="footer">Thank you for your business!</div>
      </body>
    </html>
  `;

  const printFrame = document.createElement("iframe");
  printFrame.style.position = "absolute";
  printFrame.style.width = "0";
  printFrame.style.height = "0";
  printFrame.style.border = "none";
  document.body.appendChild(printFrame);

  const doc = printFrame.contentWindow?.document;
  if (doc) {
    doc.open();
    doc.write(printContent);
    doc.close();
    printFrame.contentWindow?.focus();
    printFrame.contentWindow?.print();
  }

  setTimeout(() => {
    document.body.removeChild(printFrame);
  }, 1000);
};