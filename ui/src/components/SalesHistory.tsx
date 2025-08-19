import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Search, Eye, Calendar, DollarSign } from 'lucide-react';
import { mockApi, Sale, formatCurrency } from '@/services/mockApi';
import { useToast } from '@/hooks/use-toast';
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';

const SalesHistory = () => {
  const [sales, setSales] = useState<Sale[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedSale, setSelectedSale] = useState<Sale | null>(null);
  const [isViewDialogOpen, setIsViewDialogOpen] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const { toast } = useToast();

  const itemsPerPage = 8;

  // Load sales with pagination
  const loadSales = async (page: number = 1, search?: string) => {
    setLoading(true);
    try {
      const response = await mockApi.getSales(page, itemsPerPage, search);
      setSales(response.data);
      setTotalPages(response.totalPages);
      setCurrentPage(page);
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to load sales",
        variant: "destructive"
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSales(1, searchTerm);
  }, [searchTerm]);

  const handleSearch = (value: string) => {
    setSearchTerm(value);
    setCurrentPage(1);
  };

  const handlePageChange = (page: number) => {
    loadSales(page, searchTerm);
  };

  const openViewDialog = (sale: Sale) => {
    setSelectedSale(sale);
    setIsViewDialogOpen(true);
  };

  const getPaymentStatus = (sale: Sale) => {
    if (sale.balance === 0) {
      return { status: "Paid", variant: "default" as const };
    } else if (sale.paid > 0) {
      return { status: "Partial", variant: "secondary" as const };
    } else {
      return { status: "Pending", variant: "destructive" as const };
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Sales History</h1>
          <p className="text-muted-foreground mt-1">View all previous sales and transactions</p>
        </div>
      </div>

      {/* Search */}
      <div className="flex items-center gap-4">
        <div className="relative w-full max-w-sm">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground h-4 w-4" />
          <Input
            placeholder="Search by customer or sale ID..."
            value={searchTerm}
            onChange={(e) => handleSearch(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>

      {loading ? (
        <div className="text-center py-12">
          <p className="text-muted-foreground">Loading sales...</p>
        </div>
      ) : (
        <>
          {/* Sales List */}
          <div className="grid gap-4">
            {sales.length > 0 ? (
              sales.map((sale) => {
                const paymentInfo = getPaymentStatus(sale);
                return (
                  <Card key={sale.saleId} className="hover:shadow-md transition-shadow">
                    <CardContent className="p-6">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-6">
                          <div>
                            <h3 className="font-semibold text-lg">Sale #{sale.saleId.toString().padStart(4, '0')}</h3>
                            <p className="text-sm text-muted-foreground">{sale.customerName}</p>
                          </div>
                          <div className="flex items-center gap-2 text-sm text-muted-foreground">
                            <Calendar className="h-4 w-4" />
                            <span>{new Date(sale.saleDate).toLocaleDateString()} at {sale.saleTime}</span>
                          </div>
                          <div className="flex items-center gap-2 text-sm">
                            <DollarSign className="h-4 w-4 text-success" />
                            <span className="font-semibold text-success">{formatCurrency(sale.totalAmount)}</span>
                          </div>
                          <Badge variant={paymentInfo.variant}>
                            {paymentInfo.status}
                          </Badge>
                        </div>
                        <div className="flex items-center gap-2">
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => openViewDialog(sale)}
                          >
                            <Eye className="h-4 w-4 mr-2" />
                            View Details
                          </Button>
                        </div>
                      </div>
                      
                      {sale.saleItems.length > 0 && (
                        <div className="mt-4 pt-4 border-t">
                          <p className="text-sm text-muted-foreground">
                            Items: {sale.saleItems.map(item => `${item.item.name} (${item.qty})`).join(', ')}
                          </p>
                        </div>
                      )}
                    </CardContent>
                  </Card>
                );
              })
            ) : (
              <div className="text-center py-12">
                <p className="text-muted-foreground">
                  {searchTerm ? 'No sales found matching your search.' : 'No sales found.'}
                </p>
              </div>
            )}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex justify-center mt-8">
              <Pagination>
                <PaginationContent>
                  <PaginationItem>
                    <PaginationPrevious 
                      onClick={() => currentPage > 1 && handlePageChange(currentPage - 1)}
                      className={currentPage === 1 ? "pointer-events-none opacity-50" : "cursor-pointer"}
                    />
                  </PaginationItem>
                  {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                    <PaginationItem key={page}>
                      <PaginationLink
                        onClick={() => handlePageChange(page)}
                        isActive={currentPage === page}
                        className="cursor-pointer"
                      >
                        {page}
                      </PaginationLink>
                    </PaginationItem>
                  ))}
                  <PaginationItem>
                    <PaginationNext 
                      onClick={() => currentPage < totalPages && handlePageChange(currentPage + 1)}
                      className={currentPage === totalPages ? "pointer-events-none opacity-50" : "cursor-pointer"}
                    />
                  </PaginationItem>
                </PaginationContent>
              </Pagination>
            </div>
          )}
        </>
      )}

      {/* View Sale Dialog */}
      <Dialog open={isViewDialogOpen} onOpenChange={setIsViewDialogOpen}>
        <DialogContent className="max-w-4xl">
          <DialogHeader>
            <DialogTitle>Sale Details</DialogTitle>
          </DialogHeader>
          {selectedSale && (
            <div className="space-y-6">
              {/* Sale Info */}
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Sale ID</p>
                  <p className="text-base">#{selectedSale.saleId.toString().padStart(4, '0')}</p>
                </div>
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Customer</p>
                  <p className="text-base">{selectedSale.customerName}</p>
                </div>
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Date</p>
                  <p className="text-base">{new Date(selectedSale.saleDate).toLocaleDateString()}</p>
                </div>
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Time</p>
                  <p className="text-base">{selectedSale.saleTime}</p>
                </div>
              </div>

              {/* Items Table */}
              <div>
                <h3 className="text-lg font-semibold mb-3">Items</h3>
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Item</TableHead>
                      <TableHead>Unit Price</TableHead>
                      <TableHead>Quantity</TableHead>
                      <TableHead>Discount</TableHead>
                      <TableHead className="text-right">Total</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {selectedSale.saleItems.map((item) => (
                      <TableRow key={item.saleItemId}>
                        <TableCell className="font-medium">{item.item.name}</TableCell>
                        <TableCell>{formatCurrency(item.item.unitPrice)}</TableCell>
                        <TableCell>{item.qty}</TableCell>
                        <TableCell>{formatCurrency(item.discountAmount)}</TableCell>
                        <TableCell className="text-right">{formatCurrency(item.itemTotal)}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>

              {/* Totals */}
              <div className="border-t pt-4">
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Subtotal</p>
                    <p className="text-base font-semibold">{formatCurrency(selectedSale.subTotal)}</p>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Total Discount</p>
                    <p className="text-base font-semibold text-warning">{formatCurrency(selectedSale.totalDiscount)}</p>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Amount Paid</p>
                    <p className="text-base font-semibold text-success">{formatCurrency(selectedSale.paid)}</p>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Balance</p>
                    <p className="text-base font-semibold text-destructive">{formatCurrency(selectedSale.balance)}</p>
                  </div>
                </div>
                <div className="mt-4 pt-4 border-t">
                  <div className="flex justify-between items-center">
                    <p className="text-lg font-semibold">Total Amount</p>
                    <p className="text-2xl font-bold text-primary">{formatCurrency(selectedSale.totalAmount)}</p>
                  </div>
                </div>
              </div>

              <div className="flex justify-end">
                <Button onClick={() => setIsViewDialogOpen(false)}>
                  Close
                </Button>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
};

export { SalesHistory };