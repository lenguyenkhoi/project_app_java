-- Tạo bảng lưu đơn hàng và chi tiết (SQL Server)
IF DB_ID(N'GioHangDB') IS NULL
BEGIN
    PRINT N'Database GioHangDB chưa tồn tại. Hãy tạo DB trước hoặc chỉnh chuỗi kết nối.'
END
GO


-- Bảng Orders
IF OBJECT_ID(N'dbo.Orders', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Orders (
        OrderId INT IDENTITY(1,1) PRIMARY KEY,
        UserId INT NULL,
        ReceiverName NVARCHAR(200) NOT NULL,
        Address NVARCHAR(500) NOT NULL,
        Phone NVARCHAR(50) NOT NULL,
        Note NVARCHAR(500) NULL,
        PaymentMethod NVARCHAR(50) NOT NULL, -- COD | CARD
        Total DECIMAL(18,2) NOT NULL DEFAULT 0,
        CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
        CONSTRAINT FK_Orders_Users FOREIGN KEY (UserId) REFERENCES dbo.Users(UserId)
    );
END
GO

-- Bảng OrderItems (lưu theo tên SP)
IF OBJECT_ID(N'dbo.OrderItems', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.OrderItems (
        OrderItemId INT IDENTITY(1,1) PRIMARY KEY,
        OrderId INT NOT NULL,
        ProductCode NVARCHAR(100) NOT NULL,
        Quantity INT NOT NULL CHECK (Quantity > 0),
        PriceAtBuy DECIMAL(18,2) NOT NULL CHECK (PriceAtBuy >= 0),
        TaxAmount DECIMAL(18,2) NOT NULL DEFAULT 0, -- Thuế của cả dòng (Quantity)
        CONSTRAINT FK_OrderItems_Orders FOREIGN KEY (OrderId) REFERENCES dbo.Orders(OrderId) ON DELETE CASCADE
    );
END
GO



